package com.synectiks.school.service;

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

@Service
public class AttendanceService {
    private final Firestore firestore;

    public AttendanceService() {
        this.firestore = FirestoreClient.getFirestore();
    }


    public String storeAttendanceDetails(List<Map<String, Object>> attendanceList) {
        for (Map<String, Object> attendance : attendanceList) {
            try {
                // Extract student ID from the attendance map
                String studentId = (String) attendance.get("sid");
                String studentName = (String) attendance.get("sname");
                String clas = (String) attendance.get("class");
                List<Map<String, Object>> newAttendanceRecords = (List<Map<String, Object>>) attendance.get("attendance");

                // Get the student document reference
                DocumentReference studentRef = firestore.collection("Attendance").document(studentId);

                // Get the current document
                ApiFuture<DocumentSnapshot> future = studentRef.get();
                DocumentSnapshot document = future.get();

                if (document.exists()) {
                    // Get the current attendance array
                    Map<String, Object> currentData = document.getData();
                    List<Map<String, Object>> currentAttendanceList = (List<Map<String, Object>>) currentData.get("attendance");

                    // Append the new attendance records
                    if (currentAttendanceList == null) {
                        currentAttendanceList = new ArrayList<>();
                    }
                    currentAttendanceList.addAll(newAttendanceRecords);

                    // Update the document with the new attendance array
                    currentData.put("attendance", currentAttendanceList);
                    ApiFuture<WriteResult> writeResult = studentRef.set(currentData);
                    writeResult.get(); // Wait for the write to complete
                } else {
                    // Create a new student document with the attendance record
                    Map<String, Object> newStudentData = new HashMap<>();
                    newStudentData.put("sid", studentId);
                    newStudentData.put("sname", studentName);
                    newStudentData.put("class", clas);
                    newStudentData.put("attendance", newAttendanceRecords);
                    ApiFuture<WriteResult> writeResult = studentRef.set(newStudentData);
                    writeResult.get(); // Wait for the write to complete
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return "Error saving attendance: " + e.getMessage();
            }
        }
        return "Attendance saved successfully for all students.";
    }

    
    public String storeTeacherAttendanceDetails(Map<String, Object> attendance) {
        try {
            // Extract student ID from the attendance map
            String employeeId = (String) attendance.get("employeeId");
            String employeeName = (String) attendance.get("name");
           
//            String clas = (String) attendance.get("class");
            List<Map<String, Object>> newAttendanceRecords = (List<Map<String, Object>>) attendance.get("attendance");

            // Get the student document reference
            DocumentReference studentRef = firestore.collection("Teacher Attendance").document(employeeId);

            // Get the current document
            ApiFuture<DocumentSnapshot> future = studentRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                // Get the current attendance array
                Map<String, Object> currentData = document.getData();
                List<Map<String, Object>> currentAttendanceList = (List<Map<String, Object>>) currentData.get("attendance");

                // Append the new attendance records
                if (currentAttendanceList == null) {
                    currentAttendanceList = new ArrayList<>();
                }
                currentAttendanceList.addAll(newAttendanceRecords);

                // Update the document with the new attendance array
                currentData.put("attendance", currentAttendanceList);
                ApiFuture<WriteResult> writeResult = studentRef.set(currentData);
                return "Attendance updated successfully for student ID: " + employeeId;
            } else {
                // Create a new student document with the attendance record
                Map<String, Object> newStudentData = new HashMap<>();
                newStudentData.put("employeeId", employeeId);
                newStudentData.put("name", employeeName);
//                newStudentData.put("class", clas);
                newStudentData.put("attendance", newAttendanceRecords);
                ApiFuture<WriteResult> writeResult = studentRef.set(newStudentData);
                return "Attendance saved successfully for Employee ID: " + employeeId;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error saving attendance: " + e.getMessage();
        }
    }
    
    public List<Map<String, Object>> getAllAttendanceData() {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Get the collection reference
            CollectionReference studentsCollection = firestore.collection("Attendance");

            // Query the collection
            ApiFuture<QuerySnapshot> querySnapshot = studentsCollection.get();

            // Process the query results
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Map<String, Object> studentData = document.getData();
                results.add(studentData);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Map<String, Object>> filterAttendanceData(List<Map<String, Object>> data, String name, String period, String time) {
        List<Map<String, Object>> filteredResults = new ArrayList<>();
        for (Map<String, Object> studentData : data) {
            List<Map<String, Object>> attendanceList = (List<Map<String, Object>>) studentData.get("attendance");
            if (attendanceList != null) {
                for (Map<String, Object> attendance : attendanceList) {
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
    
    public List<Map<String, Object>> getAllTeacherAttendanceData() {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Get the collection reference
            CollectionReference teacherCollection = firestore.collection("Teacher Attendance");

            // Query the collection
            ApiFuture<QuerySnapshot> querySnapshot = teacherCollection.get();

            // Process the query results
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Map<String, Object> teacherData = document.getData();
                results.add(teacherData);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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
}
