package com.synectiks.school.service;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.io.ByteStreams;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.school.entity.AttendanceDetails;
import com.synectiks.school.entity.AttendanceRecord;

@Service
public class TeacherDetails {
	
	private static final Logger logger = LoggerFactory.getLogger(TeacherDetails.class);
    private final Firestore firestore;
	private Object schoolId;

    public TeacherDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
    

    public String addTeacher(Map<String, Object> teacherDetails,
            String className,
            String schoolId,
            String hodId,
            String uid) throws Exception {
if (teacherDetails == null) {
throw new IllegalArgumentException("Teacher details cannot be null");
}

// Verify HOD exists using hodId as the document ID in Hod_Details
DocumentReference hodDocument = firestore.collection("Hod_Details").document(hodId);
DocumentSnapshot hodSnapshot = hodDocument.get().get();
if (!hodSnapshot.exists()) {
throw new IllegalArgumentException("No HOD found with ID: " + hodId);
}

CollectionReference teacherCollection = firestore.collection("Teacher_Details");
CollectionReference schoolsCollection = firestore.collection("schools");

// Reference to users subcollection under the specific school
DocumentReference userDoc = schoolsCollection
.document(schoolId)
.collection("users")
.document(uid);

// Fetch user data from Firestore
ApiFuture<DocumentSnapshot> userFuture = userDoc.get();
DocumentSnapshot userSnapshot = userFuture.get();

if (!userSnapshot.exists()) {
throw new IllegalArgumentException("User with UID " + uid + " not found in school " + schoolId);
}

// Extract teacherId from the user document
String teacherId = userSnapshot.getString("teacherId");
if (teacherId == null) {
throw new IllegalArgumentException("No teacherId found in user document with UID " + uid);
}

// Extract relevant fields from the user document
Map<String, Object> userData = userSnapshot.getData();
if (userData != null) {
// Add user data to teacherDetails (only if not already set)
if (!teacherDetails.containsKey("email") && userData.containsKey("email")) {
teacherDetails.put("email", userData.get("email"));
}
if (!teacherDetails.containsKey("name") && userData.containsKey("name")) {
teacherDetails.put("name", userData.get("name"));
}
// Add more fields as needed based on your users collection structure
}

// Add extracted teacherId and other reference fields to teacher details
teacherDetails.put("id", teacherId);
teacherDetails.put("className", className);
teacherDetails.put("schoolId", schoolId);
teacherDetails.put("hodId", hodId);  // hodId matches the id field in Hod_Details

DocumentReference teacherDocument = teacherCollection.document(teacherId);
WriteResult result = teacherDocument.set(teacherDetails).get();

return teacherId;  // Return the teacher ID
}


    // Adding Periods for a Teacher based on Employee Name and Class
    public void addPeriods(String employeeName, String className, Map<String, Object> periods, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference periodsCollection = firestore.collection("TeacherPeriods");

        String id = UUID.randomUUID().toString();
        periods.put("id", id);
        periods.put("employeeName", employeeName);
        periods.put("className", className);
        periods.put("schoolId", schoolId);

        DocumentReference periodsDocument = periodsCollection.document(id);
        ApiFuture<WriteResult> insertingDataInDocument = periodsDocument.set(periods);
    }

    
    // Method to add a teacher's timetable
    public void addTimetableForTeacher(String employeeId, String className, Map<String, Object> timetable, String schoolId) 
            throws InterruptedException, ExecutionException {
        // Reference to the TeacherTimetables collection
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
        
        // Reference to the Teacher_Details collection to fetch the teacher's name
        CollectionReference teacherDetailsCollection = firestore.collection("Teacher_Details");
        DocumentReference teacherDocument = teacherDetailsCollection.document(employeeId);
        
        // Fetch the teacher's details synchronously
        ApiFuture<DocumentSnapshot> teacherFuture = teacherDocument.get();
        DocumentSnapshot teacherSnapshot = teacherFuture.get(); // Blocking call to get the result
        
        String teacherName = null;
        if (teacherSnapshot.exists()) {
            teacherName = teacherSnapshot.getString("name"); // Assuming "name" is the field in Teacher_Details
        } else {
            throw new IllegalArgumentException("Teacher with employeeId " + employeeId + " not found");
        }

        // Generate a unique ID for the timetable
        String id = UUID.randomUUID().toString();
        
        // Add fields to the timetable map
        timetable.put("id", id);
        timetable.put("employeeId", employeeId);
        timetable.put("className", className);
        timetable.put("schoolId", schoolId);
        timetable.put("teacherName", teacherName); // Add the teacher's name to the document

        // Save the timetable document under the employeeId
        DocumentReference timetableDocument = timetableCollection.document(employeeId);
        ApiFuture<WriteResult> insertingDataInDocument = timetableDocument.set(timetable);
    }
    
    public String storeTeacherAttendanceDetails(List<TeacherAttendanceDetails> teacherAttendanceDetailsList) {
        try {
            for (TeacherAttendanceDetails attendanceDetails : teacherAttendanceDetailsList) {
                String teacherId = attendanceDetails.getTid();
                String schoolId = attendanceDetails.getSchoolId();

                // Check if the teacher exists in the Teacher_Details collection
                DocumentReference teacherDetailsRef = firestore.collection("Teacher_Details").document(teacherId);
                ApiFuture<DocumentSnapshot> teacherDetailsFuture = teacherDetailsRef.get();
                DocumentSnapshot teacherDetailsDocument = teacherDetailsFuture.get();

                if (teacherDetailsDocument.exists()) {
                    String storedSchoolId = teacherDetailsDocument.getString("schoolId");
                    String storedHodId = teacherDetailsDocument.getString("hodId");

                    // Log the values for debugging
                    System.out.println("Teacher ID: " + teacherId);
                    System.out.println("School ID: " + schoolId);
                    System.out.println("Stored HOD ID: " + storedHodId);

                    if (schoolId.equals(storedSchoolId)) {
                        String teacherName = teacherDetailsDocument.getString("name");
                        List<com.synectiks.school.service.AttendanceRecord> newAttendanceRecords = attendanceDetails.getAttendance();

                        DocumentReference teacherRef = firestore.collection("Teacher_Attendance").document(teacherId);

                        firestore.runTransaction(transaction -> {
                            ApiFuture<DocumentSnapshot> future = transaction.get(teacherRef);
                            DocumentSnapshot document = future.get(); // Block and get the result

                            Map<String, Object> teacherData = new HashMap<>();
                            List<Map<String, Object>> currentAttendanceList = new ArrayList<>();

                            if (document.exists()) {
                                // Retrieve existing data
                                teacherData = document.getData();
                                currentAttendanceList = (List<Map<String, Object>>) teacherData.getOrDefault("attendance", new ArrayList<>());
                            } else {
                                // Set basic teacher details for a new record
                                teacherData.put("tid", teacherId);
                                teacherData.put("tname", teacherName);
                                teacherData.put("schoolId", schoolId);
                                teacherData.put("hodId", storedHodId);
                            }

                            // Convert new attendance records to Map<String, Object> and add them
                            for (com.synectiks.school.service.AttendanceRecord record : newAttendanceRecords) {
                                Map<String, Object> attendanceMap = new HashMap<>();
                                attendanceMap.put("period", record.getPeriod());
                                attendanceMap.put("time", record.getTime());
                                attendanceMap.put("present", record.isPresent());
                                currentAttendanceList.add(attendanceMap);
                            }

                            teacherData.put("attendance", currentAttendanceList);
                            transaction.set(teacherRef, teacherData);

                            return null;
                        }).get(); // Wait for transaction completion
                    } else {
                        return "School ID does not match.";
                    }
                } else {
                    return "Teacher ID does not exist.";
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Operation interrupted while saving attendance.";
        } catch (ExecutionException e) {
            return "Error saving attendance: " + e.getMessage();
        }
        return "Attendance saved successfully.";
    }
    
    public String storeDayWiseTeacherAttendanceDetails(List<TeacherAttendanceDetails> teacherAttendanceDetailsList) {
        try {
            for (TeacherAttendanceDetails attendanceDetails : teacherAttendanceDetailsList) {
                String teacherId = attendanceDetails.getTid();
                String schoolId = attendanceDetails.getSchoolId();

                // Debugging: Print the contents of attendanceDetails
                System.out.println("Teacher ID: " + teacherId);
                System.out.println("School ID: " + schoolId);
                List<com.synectiks.school.service.AttendanceRecord> newAttendanceRecords = attendanceDetails.getAttendance();
                for (com.synectiks.school.service.AttendanceRecord record : newAttendanceRecords) {
                    System.out.println("Date: " + record.getDate() + ", Present: " + record.isPresent());
                }

                // Check if the teacher exists in the Teacher_Details collection
                DocumentReference teacherDetailsRef = firestore.collection("Teacher_Details").document(teacherId);
                ApiFuture<DocumentSnapshot> teacherDetailsFuture = teacherDetailsRef.get();
                DocumentSnapshot teacherDetailsDocument = teacherDetailsFuture.get();

                if (teacherDetailsDocument.exists()) {
                    String storedSchoolId = teacherDetailsDocument.getString("schoolId");
                    String storedHodId = teacherDetailsDocument.getString("hodId");

                    // Log the values for debugging
                    System.out.println("Stored School ID: " + storedSchoolId);
                    System.out.println("Stored HOD ID: " + storedHodId);

                    if (schoolId.equals(storedSchoolId)) {
                        String teacherName = teacherDetailsDocument.getString("name");

                        // Validate that all attendance records have a date
                        for (com.synectiks.school.service.AttendanceRecord record : newAttendanceRecords) {
                            if (record.getDate() == null) {
                                return "Attendance record date is null for teacher ID: " + teacherId;
                            }
                        }

                        DocumentReference teacherRef = firestore.collection("Teacher_Day_Wise_Attendance").document(teacherId);

                        firestore.runTransaction(transaction -> {
                            ApiFuture<DocumentSnapshot> future = transaction.get(teacherRef);
                            DocumentSnapshot document = future.get(); // Block and get the result

                            Map<String, Object> teacherData = new HashMap<>();
                            List<Map<String, Object>> currentAttendanceList = new ArrayList<>();

                            if (document.exists()) {
                                // Retrieve existing data
                                teacherData = document.getData();
                                currentAttendanceList = (List<Map<String, Object>>) teacherData.getOrDefault("attendance", new ArrayList<>());
                            } else {
                                // Set basic teacher details for a new record
                                teacherData.put("tid", teacherId);
                                teacherData.put("tname", teacherName);
                                teacherData.put("schoolId", schoolId);
                                teacherData.put("hodId", storedHodId);
                            }

                            // Convert new attendance records to Map<String, Object> and add them
                            for (com.synectiks.school.service.AttendanceRecord record : newAttendanceRecords) {
                                Map<String, Object> attendanceMap = new HashMap<>();
                                attendanceMap.put("present", record.isPresent());
                                attendanceMap.put("date", record.getDate());
                                currentAttendanceList.add(attendanceMap);
                            }

                            teacherData.put("attendance", currentAttendanceList);
                            transaction.set(teacherRef, teacherData);

                            return null;
                        }).get(); // Wait for transaction completion
                    } else {
                        return "School ID does not match.";
                    }
                } else {
                    return "Teacher ID does not exist.";
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Operation interrupted while saving attendance.";
        } catch (ExecutionException e) {
            return "Error saving attendance: " + e.getMessage();
        }
        return "Attendance saved successfully.";
    }

  
    
    public List<Map<String, Object>> getDayWiseTeacherAttendanceDetails(String schoolId, String hodId, String date) {
        try {
            CollectionReference teacherAttendanceCollection = firestore.collection("Teacher_Day_Wise_Attendance");
            Query query = teacherAttendanceCollection
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("hodId", hodId);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            List<Map<String, Object>> attendanceDetailsList = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> teacherData = document.getData();
                List<Map<String, Object>> attendanceList = (List<Map<String, Object>>) teacherData.getOrDefault("attendance", new ArrayList<>());

                List<Map<String, Object>> filteredAttendanceList = attendanceList.stream()
                        .filter(attendance -> date.equals(attendance.get("date")))
                        .collect(Collectors.toList());

                if (!filteredAttendanceList.isEmpty()) {
                    Map<String, Object> filteredTeacherData = new HashMap<>(teacherData);
                    filteredTeacherData.put("attendance", filteredAttendanceList);
                    attendanceDetailsList.add(filteredTeacherData);
                }
            }

            return attendanceDetailsList;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving attendance details: " + e.getMessage(), e);
        }
    }




    
    public Map<String, Object> getTeacherAttendanceDetails(String schoolId, String teacherId, String hodId) {
        try {
            DocumentReference teacherRef = firestore.collection("Teacher_Attendance").document(teacherId);
            ApiFuture<DocumentSnapshot> teacherFuture = teacherRef.get();
            DocumentSnapshot teacherDocument = teacherFuture.get();

            if (teacherDocument.exists()) {
                Map<String, Object> teacherData = teacherDocument.getData();
                String storedSchoolId = (String) teacherData.get("schoolId");
                String storedHodId = (String) teacherData.get("hodId");

                // Log the values for debugging
                System.out.println("Teacher ID: " + teacherId);
                System.out.println("School ID: " + schoolId);
                System.out.println("HOD ID: " + hodId);
                System.out.println("Stored HOD ID: " + storedHodId);

                if (schoolId.equals(storedSchoolId) && hodId.equals(storedHodId)) {
                    return teacherData;
                } else {
                    throw new IllegalArgumentException("School ID or HOD ID does not match.");
                }
            } else {
                throw new IllegalArgumentException("Teacher ID does not exist.");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving attendance details: " + e.getMessage(), e);
        }
    }
    
    public List<Map<String, Object>> getTeacherHODAttendanceDetails(String schoolId, String hodId) {
        try {
            CollectionReference teacherAttendanceCollection = firestore.collection("Teacher_Attendance");
            Query query = teacherAttendanceCollection
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("hodId", hodId);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            List<Map<String, Object>> attendanceDetailsList = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                attendanceDetailsList.add(document.getData());
            }

            return attendanceDetailsList;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving attendance details: " + e.getMessage(), e);
        }
    }





    public void addTimetableForClass(String className, Map<String, Object> timetable, String schoolId) throws InterruptedException, ExecutionException {
        className = className.trim(); // Trim any leading or trailing spaces
        CollectionReference timetableCollection = firestore.collection("ClassTimetables");

        // Use className as the document ID
        DocumentReference timetableDocument = timetableCollection.document(className);
        timetable.put("schoolId", schoolId);
        ApiFuture<WriteResult> insertingDataInDocument = timetableDocument.set(timetable);
    }

    
    public void addClubsForClass(String className, Map<String, Object> clubsData, String schoolId) throws InterruptedException, ExecutionException {
        className = className.trim();
        clubsData.put("schoolId", schoolId);
        DocumentReference clubsDocument = firestore.collection("ClassClubs").document(className);
        ApiFuture<WriteResult> result = clubsDocument.set(clubsData);
    }




    // Getting complete Teacher Details irrespective of a specific teacher
    public List<Map<String, Object>> getAllTeacherDetails(String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }
        

        return teacherDetailsList;
    }


    // Getting Teacher Details by Teacher Name
    public List<Map<String, Object>> getTeacherDetailsByName(String teacherName) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("name", teacherName).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Teacher Details by Class Name
    public List<Map<String, Object>> getTeacherDetailsByClass(String className, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("className", className).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }


    // Getting Teacher Details by Department
    public List<Map<String, Object>> getTeacherDetailsByDepartment(String department) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("department", department).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Teacher Details by HOD Name
    public List<Map<String, Object>> getTeacherDetailsByHODName(String hodName) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("hodName", hodName).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Periods for a Teacher by Employee Name and Class
    public List<Map<String, Object>> getPeriodsByEmployeeNameAndClass(String employeeName, String className, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference periodsCollection = firestore.collection("TeacherPeriods");
        ApiFuture<QuerySnapshot> querySnapshot = periodsCollection.whereEqualTo("employeeName", employeeName).whereEqualTo("className", className).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> periodsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            periodsList.add(details.getData());
        }

        return periodsList;
    }


    // Getting Teacher Details by Employee ID and Name
    public Map<String, Object> getTeacherDetailsByEmployeeId(String id, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("id", id).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Teacher not found");
        }

        return documents.get(0).getData();
    }
    
    public List<Map<String, Object>> getAllTeacherTimetables(String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
        // Add a query to filter by schoolId
        ApiFuture<QuerySnapshot> querySnapshot = timetableCollection.whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> allTimetables = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            allTimetables.add(doc.getData());
        }
        return allTimetables;
    }



 // Method to get the teacher's timetable by employee ID
    public Map<String, Object> getTeacherTimeTable(String employeeId, String schoolId) 
    	    throws InterruptedException, ExecutionException {
    	    CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
    	    // Trim inputs to avoid whitespace issues
    	    employeeId = employeeId.trim();
    	    schoolId = schoolId.trim();
    	    
    	    ApiFuture<QuerySnapshot> querySnapshot = timetableCollection
    	        .whereEqualTo("employeeId", employeeId)
    	        .whereEqualTo("schoolId", schoolId)
    	        .get();
    	    List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

    	    if (documents.isEmpty()) {
    	        System.out.println("No documents found for employeeId: " + employeeId + 
    	                          " and schoolId: " + schoolId);
    	        return Collections.emptyMap();
    	    }
    	    
    	    System.out.println("Found " + documents.size() + " documents");
    	    return documents.get(0).getData();
    	}


    public Map<String, Object> getTimetableForClass(String className, String schoolId) throws InterruptedException, ExecutionException {
        className = className.trim(); // Trim any leading or trailing spaces
        CollectionReference timetableCollection = firestore.collection("ClassTimetables");

        // Use className as the document ID to retrieve the document
        DocumentReference timetableDocument = timetableCollection.document(className);
        ApiFuture<DocumentSnapshot> future = timetableDocument.get();

        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.getData();
        } else {
            throw new RuntimeException("Class timetable not found for className: " + className);
        }
    }





    public Map<String, Object> getTeacherDetailsByEmployeeId1(String employeeId, String name) {
        // Simulate fetching teacher details from a database or an external service
        Map<String, Object> teacherDetails = new HashMap<>();

        // Example data based on the provided image
        teacherDetails.put("attendanceThisMonth", 26);
        teacherDetails.put("totalLeaves", 2);
        teacherDetails.put("totalAvailableLeaves", 12);
        teacherDetails.put("totalAttendance", 28); // Assuming total attendance is the total working days in the month

        return teacherDetails;
    }

    // Method to get attendance and leaves by employee ID
//    public Map<String, Object> getTeacherAttendanceLeaves(String employeeId) throws InterruptedException, ExecutionException {
//        Map<String, Object> teacherDetails = getTeacherDetailsByEmployeeId(employeeId);
//
//        Map<String, Object> attendanceLeaves = new HashMap<>();
//        attendanceLeaves.put("attendanceThisMonth", teacherDetails.get("attendanceThisMonth"));
//        attendanceLeaves.put("totalLeaves", teacherDetails.get("totalLeaves"));
//        attendanceLeaves.put("totalAvailableLeaves", teacherDetails.get("totalAvailableLeaves"));
//        attendanceLeaves.put("totalAttendance", teacherDetails.get("totalAttendance"));
//
//        return attendanceLeaves;
//    }

    // Getting Time Table by Employee ID
//    public Map<String, Object> getTeacherTimeTable(String employeeId) throws InterruptedException, ExecutionException {
//        List<Map<String, Object>> periods = getPeriodsByEmployeeNameAndClass(employeeId, null);
//        if (periods.isEmpty()) {
//            throw new RuntimeException("Time table not found");
//        }
//
//        Map<String, Object> timeTable = new HashMap<>();
//        timeTable.put("timeTable", periods);
//
//        return timeTable;
//    }

//    public void addTimetableForClass(String employeeId, String className, Map<String, Object> timetable) throws InterruptedException, ExecutionException {
//        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
//
//        String id = UUID.randomUUID().toString();
//        timetable.put("id", id);
//        timetable.put("employeeId", employeeId);
//        timetable.put("className", className);
//
//        DocumentReference timetableDocument = timetableCollection.document(id);
//        ApiFuture<WriteResult> insertingDataInDocument = timetableDocument.set(timetable);
//    }

//    public List<Map<String, Object>> getTimetableForClass(String employeeId, String className) throws InterruptedException, ExecutionException {
//        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
//        ApiFuture<QuerySnapshot> querySnapshot = timetableCollection.whereEqualTo("employeeId", employeeId).whereEqualTo("className", className).get();
//        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
//        List<Map<String, Object>> timetableList = new ArrayList<>();
//
//        for (QueryDocumentSnapshot details : documents) {
//            timetableList.add(details.getData());
//        }
//
//        return timetableList;
//    }
    
    public Map<String, Object> getClubsForClass(String className) throws InterruptedException, ExecutionException {
        className = className.trim();
        DocumentReference clubsDocument = firestore.collection("ClassClubs").document(className);
        ApiFuture<DocumentSnapshot> future = clubsDocument.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.getData();
        } else {
            throw new RuntimeException("No clubs found for class: " + className);
        }
    }


    public Map<String, Object> getCombinedTimetableForTeacher(String employeeId, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
        ApiFuture<QuerySnapshot> querySnapshot = timetableCollection.whereEqualTo("employeeId", employeeId).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        Map<String, Object> combinedTimetable = new HashMap<>();

        Map<String, Map<String, String>> timetable = new HashMap<>();
        for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"}) {
            timetable.put(day, new HashMap<String, String>() {{
                put("1st period", "");
                put("2nd period", "");
                put("3rd period", "");
                put("4th period", "");
                put("5th period", "");
                put("6th period", "");
                put("7th period", "");
                put("8th period", "");
            }});
        }

        for (QueryDocumentSnapshot details : documents) {
            Map<String, Object> timetableData = details.getData();
            String className = (String) timetableData.get("className");
            Map<String, Object> periods = (Map<String, Object>) timetableData.get("periods");

            for (String day : periods.keySet()) {
                List<String> classes = (List<String>) periods.get(day);
                for (int i = 0; i < classes.size(); i++) {
                    String division = switch (i) {
                        case 0 -> "1st period";
                        case 1 -> "2nd period";
                        case 2 -> "3rd period";
                        case 3 -> "4th period";
                        case 4 -> "5th period";
                        case 5 -> "6th period";
                        case 6 -> "7th period";
                        case 7 -> "8th period";
                        default -> "";
                    };
                    timetable.get(day).put(division, classes.get(i));
                }
            }
        }

        combinedTimetable.put("timetable", timetable);
        return combinedTimetable;
    }


    // New methods for LessonPlan

 // Adding a Lesson Plan
    public void addLessonPlan(String teacherId, String schoolId, String hodId, Map<String, Object> lessonPlan)
            throws InterruptedException, ExecutionException {

        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");
        CollectionReference teacherDetailsCollection = firestore.collection("Teacher_Details");

        // Check if the teacher exists
        QuerySnapshot teacherQuerySnapshot = teacherDetailsCollection
                .whereEqualTo("id", teacherId)
                .get()
                .get();

        if (teacherQuerySnapshot.isEmpty()) {
            throw new IllegalArgumentException("Teacher with the provided ID does not exist.");
        }

        // Validate lessonPlan map
        if (lessonPlan == null) {
            throw new IllegalArgumentException("Lesson plan cannot be null.");
        }
        if (!lessonPlan.containsKey("documentBase64")) {
            throw new IllegalArgumentException("Lesson plan must contain documentBase64.");
        }

        String base64Document = (String) lessonPlan.get("documentBase64");
        if (base64Document == null || base64Document.isEmpty()) {
            throw new IllegalArgumentException("Base64 document content cannot be null or empty.");
        }

        // Generate a unique ID for the lesson plan
        String lessonPlanId = UUID.randomUUID().toString();

        // Set lesson plan fields
        lessonPlan.put("id", lessonPlanId);
        lessonPlan.put("createdAt", new Date());
        lessonPlan.put("updatedAt", new Date());
        lessonPlan.put("teacherId", teacherId);
        lessonPlan.put("schoolId", schoolId);
        lessonPlan.put("hodId", hodId);
        lessonPlan.put("status", "pending"); // Always set status as pending

        // Store in Firestore
        DocumentReference lessonPlanDocument = lessonPlanCollection.document(lessonPlanId);
        ApiFuture<WriteResult> insertingDataInDocument = lessonPlanDocument.set(lessonPlan);
        insertingDataInDocument.get();
    }



    private String convertDocumentToBase64(String documentLink) throws IOException {
        URL url = new URL(documentLink);
        try (InputStream inputStream = url.openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ByteStreams.copy(inputStream, outputStream);
            byte[] documentBytes = outputStream.toByteArray();

            // Optional: limit file size
            if (documentBytes.length > (5 * 1024 * 1024)) { // 5 MB limit
                throw new IllegalArgumentException("Document size exceeds 5MB limit.");
            }

            return Base64.getEncoder().encodeToString(documentBytes);
        } catch (IOException e) {
            System.err.println("Failed to convert document to Base64: " + e.getMessage());
            throw e;
        }
    }



    public List<Map<String, Object>> getLessonPlan(String id, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");

        ApiFuture<QuerySnapshot> querySnapshot = lessonPlanCollection
            .whereEqualTo("teacherId", id)
            .whereEqualTo("schoolId", schoolId)
            .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Lesson plans not found for employee ID: " + id + " and school ID: " + schoolId);
        }

        List<Map<String, Object>> lessonPlans = new ArrayList<>();
        SimpleDateFormat istDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        istDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> lessonPlan = document.getData();

            Timestamp createdAtTimestamp = (Timestamp) lessonPlan.get("createdAt");
            Timestamp updatedAtTimestamp = (Timestamp) lessonPlan.get("updatedAt");

            lessonPlan.put("createdAt", createdAtTimestamp != null ? istDateFormat.format(createdAtTimestamp.toDate()) : "N/A");
            lessonPlan.put("updatedAt", updatedAtTimestamp != null ? istDateFormat.format(updatedAtTimestamp.toDate()) : "N/A");

            lessonPlans.add(lessonPlan);
        }

        return lessonPlans;
    }



    public void deleteLessonPlan(String lessonPlanId, String schoolId) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> querySnapshot = firestore.collection("LessonPlans")
                .whereEqualTo("id", lessonPlanId)
                .whereEqualTo("schoolId", schoolId)
                .get();

        QuerySnapshot queryResult = querySnapshot.get();

        if (!queryResult.isEmpty()) {
            DocumentReference lessonPlanDocument = queryResult.getDocuments().get(0).getReference();
            lessonPlanDocument.delete().get();
        } else {
            throw new RuntimeException("No document found with the given lessonPlanId and schoolId.");
        }
    }



    
    public void addStudentPortfoliosForClass(String schoolId, String className, Map<String, Object> portfolioData) throws InterruptedException, ExecutionException {
        schoolId = schoolId.trim();
        className = className.trim();

        // Initialize the Firestore client
        Firestore db = FirestoreClient.getFirestore();

        // Reference to the 'StudentPortfolios' collection
        CollectionReference collectionReference = db.collection("StudentPortfolios");

        // Reference to the document within the 'StudentPortfolios' collection using the className as the document ID
        DocumentReference documentReference = collectionReference.document(className);

        // Fetch the existing document to check if it exists
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        // Initialize the portfolios array
        List<Map<String, Object>> portfolios = new ArrayList<>();

        if (document.exists()) {
            // If the document exists, get the existing portfolios array
            Map<String, Object> existingData = document.getData();
            if (existingData.containsKey("portfolios")) {
                portfolios = (List<Map<String, Object>>) existingData.get("portfolios");
            }
        }

        // Add the new portfolio to the array
        portfolios.add(portfolioData);

        // Create a map to update the document with the new portfolios array and schoolId
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("portfolios", portfolios);
        updateData.put("schoolId", schoolId);

        // Set the data in the document
        ApiFuture<WriteResult> result = documentReference.set(updateData);

        // Optionally, you can wait for the operation to complete and handle the result
        WriteResult writeResult = result.get();
        System.out.println("Update time: " + writeResult.getUpdateTime());
    }

    
    public Map<String, Object> getStudentPortfoliosForClass(String schoolId, String className) throws InterruptedException, ExecutionException {
        schoolId = schoolId.trim();
        className = className.trim();

        // Step 1: Fetch student details from Student_Details collection using schoolId
        ApiFuture<QuerySnapshot> studentDetailsFuture = firestore.collection("Student_Details")
                .whereEqualTo("schoolId", schoolId)
                .get();
        QuerySnapshot studentDetailsSnapshot = studentDetailsFuture.get();

        if (studentDetailsSnapshot.isEmpty()) {
            throw new RuntimeException("No student details found for school ID: " + schoolId);
        }

        // Step 2: Extract name and class from the fetched student details
        DocumentSnapshot studentDetailsDocument = null;
        for (DocumentSnapshot document : studentDetailsSnapshot.getDocuments()) {
            if (document.contains("studentName") && document.contains("studentClass")) {
                studentDetailsDocument = document;
                break;
            }
        }

        if (studentDetailsDocument == null) {
            throw new RuntimeException("No valid student details found for school ID: " + schoolId);
        }

        String name = studentDetailsDocument.getString("studentName");
        String classFromDetails = studentDetailsDocument.getString("studentClass");

        // Step 3: Fetch student portfolios from StudentPortfolios collection using name and class
        DocumentReference portfoliosDocument = firestore.collection("StudentPortfolios").document(classFromDetails);
        ApiFuture<DocumentSnapshot> future = portfoliosDocument.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Map<String, Object> data = document.getData();
            if (data.containsKey("schoolId") && data.get("schoolId").equals(schoolId)) {
                return data;
            } else {
                throw new RuntimeException("No portfolios found for school ID: " + schoolId + " and class: " + classFromDetails);
            }
        } else {
            throw new RuntimeException("No portfolios found for class: " + classFromDetails);
        }
    }

        
    public void updateLessonPlan(String lessonPlanId, String teacherId, String schoolId, String hodId, Map<String, Object> updatedFields)
            throws InterruptedException, ExecutionException, IOException {

        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");
        CollectionReference teacherDetailsCollection = firestore.collection("Teacher_Details");

        // Check if the teacher exists
        QuerySnapshot teacherQuerySnapshot = teacherDetailsCollection
                .whereEqualTo("id", teacherId)
                .get()
                .get();

        if (teacherQuerySnapshot.isEmpty()) {
            throw new IllegalArgumentException("Teacher with the provided ID does not exist.");
        }

        // Check if the lesson plan exists
        DocumentReference lessonPlanDocument = lessonPlanCollection.document(lessonPlanId);
        ApiFuture<DocumentSnapshot> future = lessonPlanDocument.get();
        DocumentSnapshot document = future.get();

        if (!document.exists()) {
            throw new IllegalArgumentException("Lesson plan with the provided ID does not exist.");
        }

        // Validate updatedFields map
        if (updatedFields == null || updatedFields.isEmpty()) {
            throw new IllegalArgumentException("Updated fields cannot be null or empty.");
        }

        // Update fields if they are provided in the updatedFields map
        if (updatedFields.containsKey("topicName")) {
            lessonPlanDocument.update("topicName", updatedFields.get("topicName"));
        }
        if (updatedFields.containsKey("className")) {
            lessonPlanDocument.update("className", updatedFields.get("className"));
        }
        if (updatedFields.containsKey("lessonPlanType")) {
            lessonPlanDocument.update("lessonPlanType", updatedFields.get("lessonPlanType"));
        }
        if (updatedFields.containsKey("documentBase64")) {
            String base64Document = (String) updatedFields.get("documentBase64");
            lessonPlanDocument.update("documentBase64", base64Document);
        }

        // Update the status to "pending"
        lessonPlanDocument.update("status", "pending");

        // Update the updatedAt field
        lessonPlanDocument.update("updatedAt", new Date());
    }

        
        
        public Map<String, Object> uploadDocument(String teacherId, String classNumber, byte[] fileContent, String fileName, String fileType, String schoolId)
                throws IOException, InterruptedException, ExecutionException {

            logger.info("Starting document upload process for teacherId: {}, classNumber: {}, schoolId: {}", teacherId, classNumber, schoolId);

            // Split classNumber (e.g., "10A") into number and section
            String classNum = classNumber.replaceAll("[^0-9]", "");
            String classSection = classNumber.replaceAll("[^A-Z]", "");
            logger.info("Class number: {}, Class section: {}", classNum, classSection);

            if (classNum.isEmpty() || classSection.isEmpty()) {
                logger.error("Invalid classNumber format: {}. Expected format like '10A'", classNumber);
                throw new IllegalArgumentException("Invalid classNumber format: " + classNumber + ". Expected format like '10A'");
            }

            // Validate teacher exists and get designation
            DocumentReference teacherDoc = firestore.collection("Teacher_Details").document(teacherId);
            DocumentSnapshot teacherSnapshot = teacherDoc.get().get();
            if (!teacherSnapshot.exists()) {
                logger.error("Teacher not found with ID: {}", teacherId);
                throw new IllegalArgumentException("Teacher not found with ID: " + teacherId);
            }

            // Log the entire document to verify its structure
            logger.info("Teacher Document: {}", teacherSnapshot.getData());

            List<String> allocatedClasses = (List<String>) teacherSnapshot.get("allottedClasses");
            String designation = teacherSnapshot.getString("designation");

            logger.info("Allocated Classes: {}", allocatedClasses);
            logger.info("Designation: {}", designation);

            if (allocatedClasses == null || !allocatedClasses.contains(classNumber)) {
                logger.error("Class {} not allocated to teacher {}", classNumber, teacherId);
                throw new IllegalArgumentException("Class " + classNumber + " not allocated to teacher " + teacherId);
            }
            if (designation == null) {
                logger.error("Teacher designation not found for teacher ID: {}", teacherId);
                throw new IllegalArgumentException("Teacher designation not found");
            }

            // Extract subject from designation (e.g., "Maths Teacher" -> "Maths")
            String subject = designation.replace("Teacher", "").trim();
            logger.info("Extracted subject: {}", subject);

            // Verify school exists
            DocumentReference schoolDoc = firestore.collection("schools").document(schoolId);
            DocumentSnapshot schoolSnapshot = schoolDoc.get().get();
            if (!schoolSnapshot.exists()) {
                logger.error("School not found with ID: {}", schoolId);
                throw new IllegalArgumentException("School not found with ID: " + schoolId);
            }

            // Prepare document data (only required fields)
            Map<String, Object> documentData = new HashMap<>();
            documentData.put("fileName", fileName);
            documentData.put("fileType", fileType);
            documentData.put("data", Base64.getEncoder().encodeToString(fileContent));
            documentData.put("subject", subject);
            logger.info("Prepared document data: {}", documentData);

            // Check if subject collection exists, create if not
            CollectionReference subjectsCollection = firestore.collection("Class_Documents")
                .document(classNum)
                .collection("sections")
                .document(classSection)
                .collection("subjects");

            DocumentReference subjectDoc = subjectsCollection.document(subject);
            DocumentSnapshot subjectSnapshot = subjectDoc.get().get();
            if (!subjectSnapshot.exists()) {
                // Create subject document with metadata
                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("teacherId", teacherId);
                subjectData.put("schoolId", schoolId);
                subjectData.put("classNumber", classNum);
                subjectData.put("section", classSection);
                subjectData.put("createdAt", Timestamp.now());
                subjectDoc.set(subjectData).get();
                logger.info("Created new subject document: {}", subjectData);
            }

            // Add document to documents subcollection
            CollectionReference documentsCollection = subjectDoc.collection("documents");
            DocumentReference docRef = documentsCollection.document(); // Random ID
            WriteResult result = docRef.set(documentData).get();
            logger.info("Document uploaded successfully with ID: {}", docRef.getId());

            // Prepare response
            Map<String, Object> response = new HashMap<>(documentData);
            response.put("id", docRef.getId());
            response.put("classNumber", classNum);
            response.put("section", classSection);
            logger.info("Prepared response: {}", response);

            return response;
        }
        
        
        public List<Map<String, Object>> getHomework(String classNumber, String schoolId)
                throws InterruptedException, ExecutionException {

            logger.info("Fetching homework for studentId: {}, classNumber: {}, schoolId: {}",classNumber, schoolId);

            // Split classNumber (e.g., "10A") into number and section
            String classNum = classNumber.replaceAll("[^0-9]", "");
            String classSection = classNumber.replaceAll("[^A-Z]", "");
            logger.info("Class number: {}, Class section: {}", classNum, classSection);

            if (classNum.isEmpty() || classSection.isEmpty()) {
                logger.error("Invalid classNumber format: {}. Expected format like '10A'", classNumber);
                throw new IllegalArgumentException("Invalid classNumber format: " + classNumber + ". Expected format like '10A'");
            }

            // Verify school exists
            DocumentReference schoolDoc = firestore.collection("schools").document(schoolId);
            DocumentSnapshot schoolSnapshot = schoolDoc.get().get();
            if (!schoolSnapshot.exists()) {
                logger.error("School not found with ID: {}", schoolId);
                throw new IllegalArgumentException("School not found with ID: " + schoolId);
            }

            // Fetch homework documents
            CollectionReference subjectsCollection = firestore.collection("Class_Documents")
                .document(classNum)
                .collection("sections")
                .document(classSection)
                .collection("subjects");

            List<Map<String, Object>> homeworkList = new ArrayList<>();
            QuerySnapshot subjectsSnapshot = subjectsCollection.get().get();
            for (DocumentSnapshot subjectDoc : subjectsSnapshot.getDocuments()) {
                CollectionReference documentsCollection = subjectDoc.getReference().collection("documents");
                QuerySnapshot documentsSnapshot = documentsCollection.get().get();
                for (DocumentSnapshot doc : documentsSnapshot.getDocuments()) {
                    Map<String, Object> homework = doc.getData();
                    homework.put("id", doc.getId());
                    homeworkList.add(homework);
                }
            }

            logger.info("Fetched homework list: {}", homeworkList);
            return homeworkList;
        }    
}
