package com.synectiks.school.service;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class TeacherDetails {

    private final Firestore firestore;
	private Object schoolId;

    public TeacherDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
    

    public String addTeacher(Map<String, Object> teacherDetails,
            String className,
            String schoolId,
            String hodId) throws Exception {
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

// Generate random UUID for the teacher
String teacherId = UUID.randomUUID().toString();

// Add generated ID and other reference fields to teacher details
teacherDetails.put("id", teacherId);
teacherDetails.put("className", className);
teacherDetails.put("schoolId", schoolId);
teacherDetails.put("hodId", hodId);  // hodId matches the id field in Hod_Details

DocumentReference teacherDocument = teacherCollection.document(teacherId);
WriteResult result = teacherDocument.set(teacherDetails).get();

return teacherId;  // Return the generated teacher ID
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
    public void addTimetableForTeacher(String employeeId, String className, Map<String, Object> timetable, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");

        String id = UUID.randomUUID().toString();
        timetable.put("id", id);
        timetable.put("employeeId", employeeId);
        timetable.put("className", className);
        timetable.put("schoolId", schoolId);

        DocumentReference timetableDocument = timetableCollection.document(id);
        ApiFuture<WriteResult> insertingDataInDocument = timetableDocument.set(timetable);
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



 // Method to get the teacher's timetable by employee ID
    public Map<String, Object> getTeacherTimeTable(String employeeId, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
        ApiFuture<QuerySnapshot> querySnapshot = timetableCollection.whereEqualTo("employeeId", employeeId).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Time table not found for employee ID: " + employeeId);
        }

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
            throw new IllegalArgumentException("Teacher with the provided id does not exist.");
        }

        // Generate a unique ID for the lesson plan
        String lessonPlanId = UUID.randomUUID().toString();

        // Set the default values for the lesson plan
        lessonPlan.put("id", lessonPlanId);
        lessonPlan.put("createdAt", new Date());
        lessonPlan.put("updatedAt", new Date());
        lessonPlan.put("teacherId", teacherId);
        lessonPlan.put("schoolId", schoolId);
        lessonPlan.put("hodId", hodId);
        lessonPlan.put("status", "pending"); // Always set status as pending

        // Add the lesson plan to the Firestore collection
        DocumentReference lessonPlanDocument = lessonPlanCollection.document(lessonPlanId);
        ApiFuture<WriteResult> insertingDataInDocument = lessonPlanDocument.set(lessonPlan);
        insertingDataInDocument.get();
    }

    public List<Map<String, Object>> getLessonPlan(String id, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");

        // Query the collection to find documents with the matching 'teacherId' and 'schoolId'
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
        istDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone

        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> lessonPlan = document.getData();

            // Convert Timestamp to Date and format
            Timestamp createdAtTimestamp = (Timestamp) lessonPlan.get("createdAt");
            Timestamp updatedAtTimestamp = (Timestamp) lessonPlan.get("updatedAt");

            if (createdAtTimestamp != null) {
                lessonPlan.put("createdAt", istDateFormat.format(new Date(createdAtTimestamp.getSeconds() * 1000)));
            } else {
                lessonPlan.put("createdAt", "N/A"); // Handle null case
            }

            if (updatedAtTimestamp != null) {
                lessonPlan.put("updatedAt", istDateFormat.format(new Date(updatedAtTimestamp.getSeconds() * 1000)));
            } else {
                lessonPlan.put("updatedAt", "N/A"); // Handle null case
            }

            lessonPlans.add(lessonPlan);
        }

        return lessonPlans;
    }


    public void deleteLessonPlan(String lessonPlanId, String schoolId) throws InterruptedException, ExecutionException {
        // Query the collection to find the document with the matching 'lessonPlanId' and 'schoolId'
        ApiFuture<QuerySnapshot> querySnapshot = firestore.collection("LessonPlans")
                .whereEqualTo("id", lessonPlanId)
                .whereEqualTo("schoolId", schoolId)
                .get();
        QuerySnapshot queryResult = querySnapshot.get();

        if (!queryResult.isEmpty()) {
            // Get the first document that matches the query
            DocumentSnapshot documentSnapshot = queryResult.getDocuments().get(0);
            DocumentReference lessonPlanDocument = documentSnapshot.getReference();

            // Delete the document
            ApiFuture<WriteResult> deleteResult = lessonPlanDocument.delete();
            deleteResult.get(); // Wait for the delete to complete
        } else {
            System.out.println("No document found with the given lessonPlanId and schoolId.");
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

        DocumentReference portfoliosDocument = firestore.collection("StudentPortfolios").document(className);
        ApiFuture<DocumentSnapshot> future = portfoliosDocument.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Map<String, Object> data = document.getData();
            if (data.containsKey("schoolId") && data.get("schoolId").equals(schoolId)) {
                return data;
            } else {
                throw new RuntimeException("No portfolios found for school ID: " + schoolId + " and class: " + className);
            }
        } else {
            throw new RuntimeException("No portfolios found for class: " + className);
        }
    }

        
        public Map<String, Object> updateLessonPlan(String id, String schoolId, Map<String, Object> lessonPlan) 
                throws InterruptedException, ExecutionException {
            // Create a response map to return
            Map<String, Object> response = new HashMap<>();
            
            // Query the collection to find the document with the matching 'id' field and 'schoolId'
            ApiFuture<QuerySnapshot> querySnapshot = firestore.collection("LessonPlans")
                    .whereEqualTo("id", id)
                    .whereEqualTo("schoolId", schoolId)
                    .get();
            QuerySnapshot queryResult = querySnapshot.get();

            if (!queryResult.isEmpty()) {
                // Get the first document that matches the query
                DocumentSnapshot documentSnapshot = queryResult.getDocuments().get(0);
                DocumentReference lessonPlanDocument = documentSnapshot.getReference();

                // Add the required fields to the lessonPlan map
                lessonPlan.put("id", id);
                lessonPlan.put("schoolId", schoolId);
                lessonPlan.put("status", "pending"); // Ensure status is always pending
                
                // Get hodId and teacherId from the existing document
                String hodId = documentSnapshot.getString("hodId");
                String teacherId = documentSnapshot.getString("teacherId");
                
                // Add hodId and teacherId to lessonPlan if they exist
                if (hodId != null) lessonPlan.put("hodId", hodId);
                if (teacherId != null) lessonPlan.put("teacherId", teacherId);
                
                // Update the document with the new data
                ApiFuture<WriteResult> updatingDataInDocument = lessonPlanDocument.set(lessonPlan);
                WriteResult writeResult = updatingDataInDocument.get();
                Timestamp updateTime = writeResult.getUpdateTime();

                // Add the update time to the lessonPlan map
                lessonPlan.put("UpdatedAt", updateTime);
                lessonPlanDocument.update("UpdatedAt", updateTime);

                // Prepare the response
                response.put("hodId", hodId);
                response.put("teacherId", teacherId);
                response.put("lessonPlan", lessonPlan);
                response.put("updateTime", updateTime.toString());
            } else {
                // Prepare response for no document found
                response.put("hodId", null);
                response.put("teacherId", null);
                response.put("lessonPlan", null);
                response.put("updateTime", null);
            }
            
            return response;
        }
    

    
    
}
