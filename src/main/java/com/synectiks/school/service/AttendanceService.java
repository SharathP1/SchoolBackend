package com.synectiks.school.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.school.entity.AttendanceDetails;
import com.synectiks.school.entity.AttendanceRecord;
import com.synectiks.school.entity.StudentAttendance;
import com.synectiks.school.entity.TeacherAttendance;

@Service
public class AttendanceService {
    private final Firestore firestore;

    public AttendanceService() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
    

    public String storeAttendanceDetails(AttendanceDetails attendanceDetails) {
        try {
            String studentId = attendanceDetails.getSid();
            String schoolId = attendanceDetails.getSchoolId();

            // Check if the student exists in the Student_Details collection
            DocumentReference studentDetailsRef = firestore.collection("Student_Details").document(studentId);
            ApiFuture<DocumentSnapshot> studentDetailsFuture = studentDetailsRef.get();
            DocumentSnapshot studentDetailsDocument = studentDetailsFuture.get();

            if (studentDetailsDocument.exists() &&
                schoolId.equals(studentDetailsDocument.getString("schoolId")) &&
                studentId.equals(studentDetailsDocument.getString("id"))) {

                String studentName = attendanceDetails.getSname();
                String clas = attendanceDetails.getStudentClass();
                List<AttendanceRecord> newAttendanceRecords = attendanceDetails.getAttendance();

                DocumentReference studentRef = firestore.collection("Attendance").document(studentId);

                firestore.runTransaction(transaction -> {
                    ApiFuture<DocumentSnapshot> future = transaction.get(studentRef);
                    DocumentSnapshot document = future.get(); // Block and get the result

                    Map<String, Object> studentData = new HashMap<>();
                    List<Map<String, Object>> currentAttendanceList = new ArrayList<>();

                    if (document.exists()) {
                        // Retrieve existing data
                        studentData = document.getData();
                        currentAttendanceList = (List<Map<String, Object>>) studentData.getOrDefault("attendance", new ArrayList<>());
                    } else {
                        // Set basic student details for a new record
                        studentData.put("sid", studentId);
                        studentData.put("sname", studentName);
                        studentData.put("class", clas);
                        studentData.put("schoolId", schoolId);
                    }

                    // Convert new attendance records to Map<String, Object> and add them
                    for (AttendanceRecord record : newAttendanceRecords) {
                        Map<String, Object> attendanceMap = new HashMap<>();
                        attendanceMap.put("period", record.getPeriod());
                        attendanceMap.put("time", record.getTime());
                        attendanceMap.put("present", record.isPresent());
                        currentAttendanceList.add(attendanceMap);
                    }

                    studentData.put("attendance", currentAttendanceList);
                    transaction.set(studentRef, studentData);

                    return null;
                }).get(); // Wait for transaction completion
            } else {
                return "Student ID, School ID, or ID does not match.";
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Operation interrupted while saving attendance.";
        } catch (ExecutionException e) {
            return "Error saving attendance: " + e.getMessage();
        }
        return "Attendance saved successfully.";
    }




    public String storeDayAttendanceDetails(List<StudentAttendance> attendanceList) {
        for (StudentAttendance attendance : attendanceList) {
            try {
                String studentId = attendance.getSid();
                String schoolId = attendance.getSchoolId();

                // Check if the student exists in the Student_Details collection
                DocumentReference studentDetailsRef = firestore.collection("Student_Details").document(studentId);
                ApiFuture<DocumentSnapshot> studentDetailsFuture = studentDetailsRef.get();
                DocumentSnapshot studentDetailsDocument = studentDetailsFuture.get();

                if (studentDetailsDocument.exists() &&
                    schoolId.equals(studentDetailsDocument.getString("schoolId")) &&
                    studentId.equals(studentDetailsDocument.getString("id"))) {

                    String studentName = attendance.getSname();
                    String clas = attendance.getStudentClass();
                    List<AttendanceRecord> newAttendanceRecords = attendance.getAttendance();

                    DocumentReference studentRef = firestore.collection("Day-Wise-Attendance").document(studentId);

                    firestore.runTransaction(transaction -> {
                        ApiFuture<DocumentSnapshot> future = transaction.get(studentRef);
                        DocumentSnapshot document = future.get(); // Block and get the result

                        Map<String, Object> studentData = new HashMap<>();
                        List<Map<String, Object>> currentAttendanceList = new ArrayList<>();

                        if (document.exists()) {
                            // Retrieve existing data
                            studentData = document.getData();
                            currentAttendanceList = (List<Map<String, Object>>) studentData.getOrDefault("attendance", new ArrayList<>());
                        } else {
                            // Set basic student details for a new record
                            studentData.put("sid", studentId);
                            studentData.put("sname", studentName);
                            studentData.put("class", clas);
                            studentData.put("schoolId", schoolId);
                        }

                        // Convert new attendance records to Map<String, Object> and add them
                        for (AttendanceRecord record : newAttendanceRecords) {
                            Map<String, Object> attendanceMap = new HashMap<>();
                            attendanceMap.put("time", record.getTime());
                            attendanceMap.put("present", record.isPresent());
                            currentAttendanceList.add(attendanceMap);
                        }

                        studentData.put("attendance", currentAttendanceList);
                        transaction.set(studentRef, studentData);

                        return null;
                    }).get(); // Wait for transaction completion
                } else {
                    return "Student ID, School ID, or ID does not match.";
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Operation interrupted while saving attendance.";
            } catch (ExecutionException e) {
                return "Error saving attendance: " + e.getMessage();
            }
        }
        return "Attendance saved successfully for all students.";
    }

    
    public String storeTeacherAttendanceDetails(TeacherAttendance attendance) {
        try {
            String employeeId = attendance.getEmployeeId();
            String employeeName = attendance.getName();
            String schoolId = attendance.getSchoolId();
            List<AttendanceRecord> newAttendanceRecords = attendance.getAttendance();

            DocumentReference teacherRef = firestore.collection("Teacher_Attendance").document(employeeId);

            ApiFuture<DocumentSnapshot> future = teacherRef.get();
            DocumentSnapshot document = future.get();

            Map<String, Object> teacherData = new HashMap<>();
            List<Map<String, Object>> currentAttendanceList;

            if (document.exists()) {
                teacherData = document.getData();
                currentAttendanceList = (List<Map<String, Object>>) teacherData.getOrDefault("attendance", new ArrayList<>());
            } else {
                teacherData.put("employeeId", employeeId);
                teacherData.put("name", employeeName);
                teacherData.put("schoolId", schoolId);
                currentAttendanceList = new ArrayList<>();
            }

            for (AttendanceRecord record : newAttendanceRecords) {
                Map<String, Object> attendanceMap = new HashMap<>();
                attendanceMap.put("period", record.getPeriod());
                attendanceMap.put("time", record.getTime());
//                attendanceMap.put("present", record.isPresent());
                currentAttendanceList.add(attendanceMap);
            }

            teacherData.put("attendance", currentAttendanceList);
            ApiFuture<WriteResult> writeResult = teacherRef.set(teacherData);
            return "Attendance saved successfully for Employee ID: " + employeeId;

        } catch (InterruptedException | ExecutionException e) {
            
            return "Error saving attendance: " + e.getMessage();
        }
    }
    
    public List<Map<String, Object>> getAllAttendanceData(String schoolId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Get the collection reference
            CollectionReference attendanceCollection = firestore.collection("Attendance");

            // Create a query against the collection
            ApiFuture<QuerySnapshot> querySnapshot = attendanceCollection.whereEqualTo("schoolId", schoolId).get();

            // Process the query results
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Map<String, Object> studentData = document.getData();
                results.add(studentData);
            }
        } catch (InterruptedException | ExecutionException e) {
         
        }
        return results;
    }
    
    public List<Map<String, Object>> getAllDayAttendanceData(String schoolId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Get the collection reference
            CollectionReference attendanceCollection = firestore.collection("Day-Wise-Attendance");

            // Create a query against the collection
            ApiFuture<QuerySnapshot> querySnapshot = attendanceCollection.whereEqualTo("schoolId", schoolId).get();

            // Process the query results
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Map<String, Object> studentData = document.getData();
                results.add(studentData);
            }
        } catch (InterruptedException | ExecutionException e) {
         
        }
        return results;
    }
    
    public List<Map<String, Object>> getAttendanceData(String schoolId, String sid) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Log the input parameters
            System.out.println("Fetching attendance data for schoolId: " + schoolId + " and sid: " + sid);

            // Get the collection reference
            CollectionReference attendanceCollection = firestore.collection("Attendance");
            System.out.println("Collection reference obtained: " + attendanceCollection.getId());

            // Create a query against the collection
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = attendanceCollection
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("sid", sid)
                    .get();
            System.out.println("Query created and executed.");

            // Process the query results
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
            System.out.println("Query snapshot obtained. Number of documents: " + querySnapshot.size());

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> studentData = document.getData();
                results.add(studentData);
                System.out.println("Document data: " + studentData);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Handle the exception appropriately
            System.err.println("Error fetching attendance data: " + e.getMessage());
        }
        return results;
    }
    
    public List<Map<String, Object>> getDayAttendanceData(String schoolId, String sid) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Log the input parameters
            System.out.println("Fetching attendance data for schoolId: " + schoolId + " and sid: " + sid);

            // Get the collection reference
            CollectionReference attendanceCollection = firestore.collection("Day-Wise-Attendance");
            System.out.println("Collection reference obtained: " + attendanceCollection.getId());

            // Create a query against the collection
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = attendanceCollection
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("sid", sid)
                    .get();
            System.out.println("Query created and executed.");

            // Process the query results
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
            System.out.println("Query snapshot obtained. Number of documents: " + querySnapshot.size());

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> studentData = document.getData();
                results.add(studentData);
                System.out.println("Document data: " + studentData);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Handle the exception appropriately
            System.err.println("Error fetching attendance data: " + e.getMessage());
        }
        return results;
    }
    
    public List<Map<String, Object>> getDayAttendanceDataDateWise(String schoolId, String sid, String requestedDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Ensure date format matches

        try {
            System.out.println("Fetching attendance for schoolId: " + schoolId + ", sid: " + sid + ", date: " + requestedDate);

            CollectionReference attendanceCollection = firestore.collection("Day-Wise-Attendance");

            ApiFuture<QuerySnapshot> querySnapshotApiFuture = attendanceCollection
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("sid", sid)
                    .get();

            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> studentData = document.getData();
                List<Map<String, Object>> attendanceList = (List<Map<String, Object>>) studentData.get("attendance");

                List<Map<String, Object>> filteredAttendance = new ArrayList<>();
                for (Map<String, Object> record : attendanceList) {
                    String recordTime = (String) record.get("time"); // Get stored timestamp
                    String recordDate = recordTime.substring(0, 10); // Extract yyyy-MM-dd

                    if (recordDate.equals(requestedDate)) { // Compare with requested date
                        filteredAttendance.add(record);
                    }
                }

                if (!filteredAttendance.isEmpty()) {
                    Map<String, Object> resultEntry = new HashMap<>(studentData);
                    resultEntry.put("attendance", filteredAttendance); // Store only matching records
                    results.add(resultEntry);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.err.println("Error fetching attendance data: " + e.getMessage());
        }
        return results;
    }
    
    
    public List<Map<String, Object>> getMonthAttendanceData(String schoolId, String sid, String requestedMonth) {
        List<Map<String, Object>> results = new ArrayList<>();

        try {
            System.out.println("Fetching attendance for schoolId: " + schoolId + ", sid: " + sid + ", month: " + requestedMonth);

            CollectionReference attendanceCollection = firestore.collection("Day-Wise-Attendance");

            ApiFuture<QuerySnapshot> querySnapshotApiFuture = attendanceCollection
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("sid", sid)
                    .get();

            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> studentData = document.getData();
                List<Map<String, Object>> attendanceList = (List<Map<String, Object>>) studentData.get("attendance");

                List<Map<String, Object>> filteredAttendance = new ArrayList<>();
                for (Map<String, Object> record : attendanceList) {
                    String recordTime = (String) record.get("time"); // Get stored timestamp
                    String recordMonth = recordTime.substring(0, 7); // Extract yyyy-MM

                    if (recordMonth.equals(requestedMonth)) { // Compare with requested month (yyyy-MM)
                        filteredAttendance.add(record);
                    }
                }

                if (!filteredAttendance.isEmpty()) {
                    Map<String, Object> resultEntry = new HashMap<>(studentData);
                    resultEntry.put("attendance", filteredAttendance); // Store only matching records
                    results.add(resultEntry);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.err.println("Error fetching attendance data: " + e.getMessage());
        }
        return results;
    }

    
    public List<Map<String, Object>> filterAttendanceData(List<Map<String, Object>> data, String name, String period, String time) {
        List<Map<String, Object>> filteredResults = new ArrayList<>();
        for (Map<String, Object> studentData : data) {
            List<Map<String, Object>> attendanceList = (List<Map<String, Object>>) studentData.get("attendance");
            if (attendanceList != null) {
                for (Map<String, Object> attendance : attendanceList) {
                	String studentid = (String) attendance.get("sid");
                	String attendanceName = (String) attendance.get("name");
                    String attendancePeriod = (String) attendance.get("period");
                    String attendanceTime = (String) attendance.get("time");
                    if ((period == null || period.equals(attendancePeriod)) &&
                        (time == null || time.equals(attendanceTime))) {
                        filteredResults.add(attendance);
                    }
                }
            }
        }
        return filteredResults;
    }
    
    public List<Map<String, Object>> getAllTeacherAttendanceData(String schoolId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Get the collection reference
            CollectionReference teacherCollection = firestore.collection("Teacher_Attendance");

            // Create a query against the collection
            ApiFuture<QuerySnapshot> querySnapshot = teacherCollection.whereEqualTo("schoolId", schoolId).get();

            // Process the query results
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Map<String, Object> teacherData = document.getData();
                results.add(teacherData);
            }
        } catch (InterruptedException | ExecutionException e) {
        }
        return results;
    }

    public List<Map<String, Object>> filterTeacherAttendanceData(List<Map<String, Object>> data, String time, String name) {
        List<Map<String, Object>> filteredResults = new ArrayList<>();
        for (Map<String, Object> teacherData : data) {
            List<Map<String, Object>> attendanceList = (List<Map<String, Object>>) teacherData.get("attendance");
            if (attendanceList != null) {
                for (Map<String, Object> attendance : attendanceList) {
                	String attendanceName = (String) attendance.get("name");
                    String attendanceTime = (String) attendance.get("time");
                    if (time == null || time.equals(attendanceTime)) {
                        filteredResults.add(attendance);
                    }
                }
            }
        }
        return filteredResults;
    }

	public List<Map<String, Object>> getstudentAttendanceData(String schoolId, String studentClass) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Get the collection reference
            CollectionReference attendanceCollection = firestore.collection("Attendance");

            // Create a query against the collection
            ApiFuture<QuerySnapshot> querySnapshot = attendanceCollection
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("class", studentClass)
                    .get();

            // Process the query results
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Map<String, Object> studentData = document.getData();
                results.add(studentData);
            }
        } catch (InterruptedException | ExecutionException e) {
            
        }
        return results;
    }
}
