package com.synectiks.school.service;

import com.google.api.core.ApiFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class LeaveRequestService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestService.class);

    private final Firestore firestore;

    public LeaveRequestService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public String sendLeaveRequest(String schoolId, String studentId, String teacherId, Map<String, Object> leaveDetails)
            throws ExecutionException, InterruptedException {
        // Validate schoolId from Student_Details
        Map<String, Object> studentDetails = firestore.collection("Student_Details")
                .document(studentId).get().get().getData();
        if (studentDetails == null) {
            throw new IllegalArgumentException("Student ID not found");
        }
        String fetchedSchoolId = (String) studentDetails.get("schoolId");
        if (!fetchedSchoolId.equals(schoolId)) {
            throw new IllegalArgumentException("School ID mismatch");
        }

        // Validate teacherId
        Map<String, Object> teacherDetails = firestore.collection("Teacher_Details")
                .document(teacherId).get().get().getData();
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher ID not found");
        }

        // Create leave request document under schoolId/teacherId/studentId
        Map<String, Object> newLeaveRequest = new HashMap<>();
        newLeaveRequest.putAll(leaveDetails);
        newLeaveRequest.put("status", "pending");
        newLeaveRequest.put("schoolId", schoolId);  // Add schoolId to document
        newLeaveRequest.put("teacherId", teacherId);  // Add teacherId to document
        newLeaveRequest.put("studentId", studentId);  // Add studentId to document

        DocumentReference leaveRequestRef = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("Teachers")
                .document(teacherId)
                .collection("LeaveRequests")
                .document(studentId);

        DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();

        if (leaveRequestSnapshot.exists()) {
            // Update existing document
            Map<String, Object> existingLeaveDetails = leaveRequestSnapshot.getData();
            List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) existingLeaveDetails.get("leaveRequests");
            if (leaveRequestsList == null) {
                leaveRequestsList = new ArrayList<>();
            }
            leaveRequestsList.add(newLeaveRequest);
            leaveRequestRef.update("leaveRequests", leaveRequestsList);
        } else {
            // Create new document
            List<Map<String, Object>> leaveRequestsList = new ArrayList<>();
            leaveRequestsList.add(newLeaveRequest);
            Map<String, Object> initialLeaveRequest = new HashMap<>();
            initialLeaveRequest.put("leaveRequests", leaveRequestsList);
            leaveRequestRef.set(initialLeaveRequest);
        }

        return studentId;  // Returning studentId as the reference
    }

    public String sendTeacherLeaveRequest(String schoolId, String teacherId, String hodId, Map<String, Object> leaveDetails)
            throws ExecutionException, InterruptedException {
        // Validate teacherId from Teacher_Details
        Map<String, Object> teacherDetails = firestore.collection("Teacher_Details")
                .document(teacherId).get().get().getData();
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher ID not found");
        }
        String fetchedSchoolId = (String) teacherDetails.get("schoolId");
        if (!fetchedSchoolId.equals(schoolId)) {
            throw new IllegalArgumentException("School ID mismatch");
        }

        // Validate hodId
        Map<String, Object> hodDetails = firestore.collection("Hod_Details")
                .document(hodId).get().get().getData();
        if (hodDetails == null) {
            throw new IllegalArgumentException("HOD ID not found");
        }

        // Create leave request document under schoolId/hodId/teacherId
        Map<String, Object> newLeaveRequest = new HashMap<>();
        newLeaveRequest.putAll(leaveDetails);
        newLeaveRequest.put("status", "pending");
        newLeaveRequest.put("schoolId", schoolId);  // Add schoolId to document
        newLeaveRequest.put("hodId", hodId);  // Add hodId to document
        newLeaveRequest.put("teacherId", teacherId);  // Add teacherId to document

        DocumentReference leaveRequestRef = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("HOD")
                .document(hodId)
                .collection("LeaveRequests")
                .document(teacherId);

        DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();

        if (leaveRequestSnapshot.exists()) {
            // Update existing document
            Map<String, Object> existingLeaveDetails = leaveRequestSnapshot.getData();
            List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) existingLeaveDetails.get("leaveRequests");
            if (leaveRequestsList == null) {
                leaveRequestsList = new ArrayList<>();
            }
            leaveRequestsList.add(newLeaveRequest);
            leaveRequestRef.update("leaveRequests", leaveRequestsList);
        } else {
            // Create new document
            List<Map<String, Object>> leaveRequestsList = new ArrayList<>();
            leaveRequestsList.add(newLeaveRequest);
            Map<String, Object> initialLeaveRequest = new HashMap<>();
            initialLeaveRequest.put("leaveRequests", leaveRequestsList);
            leaveRequestRef.set(initialLeaveRequest);
        }

        return teacherId;  // Returning teacherId as the reference
    }

    public String sendHODLeaveRequest(String schoolId, String hodId, String hrId, Map<String, Object> leaveDetails)
            throws ExecutionException, InterruptedException {
        logger.info("Processing leave request for HOD ID: {}, HR ID: {}, School ID: {}", hodId, hrId, schoolId);

        // Validate HOD details
        DocumentSnapshot hodSnapshot = firestore.collection("Hod_Details").document(hodId).get().get();
        if (!hodSnapshot.exists()) {
            logger.error("HOD ID not found: {}", hodId);
            throw new IllegalArgumentException("HOD ID not found");
        }
        Map<String, Object> hodDetails = hodSnapshot.getData();
        String fetchedSchoolId = (String) hodDetails.get("schoolId");
        if (!fetchedSchoolId.equals(schoolId)) {
            logger.error("School ID mismatch for HOD ID: {}", hodId);
            throw new IllegalArgumentException("School ID mismatch");
        }

        // Validate HR details
        DocumentSnapshot hrSnapshot = firestore.collection("HR_Details").document(hrId).get().get();
        if (!hrSnapshot.exists()) {
            logger.error("HR ID not found: {}", hrId);
            throw new IllegalArgumentException("HR ID not found");
        }

        // Create leave request document
        Map<String, Object> newLeaveRequest = new HashMap<>(leaveDetails);
        newLeaveRequest.put("status", "pending");
        newLeaveRequest.put("schoolId", schoolId);
        newLeaveRequest.put("hrId", hrId);
        newLeaveRequest.put("id", hodId);

        DocumentReference leaveRequestRef = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("HR")
                .document(hrId)
                .collection("LeaveRequests")
                .document(hodId);

        DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();

        if (leaveRequestSnapshot.exists()) {
            // Update existing document
            Map<String, Object> existingLeaveDetails = leaveRequestSnapshot.getData();
            List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) existingLeaveDetails.get("leaveRequests");
            if (leaveRequestsList == null) {
                leaveRequestsList = new ArrayList<>();
            }
            leaveRequestsList.add(newLeaveRequest);
            leaveRequestRef.update("leaveRequests", leaveRequestsList);
        } else {
            // Create new document
            List<Map<String, Object>> leaveRequestsList = new ArrayList<>();
            leaveRequestsList.add(newLeaveRequest);
            Map<String, Object> initialLeaveRequest = new HashMap<>();
            initialLeaveRequest.put("leaveRequests", leaveRequestsList);
            leaveRequestRef.set(initialLeaveRequest);
        }

        logger.info("Leave request processed successfully for HOD ID: {}", hodId);
        return hodId;
    }

    public List<Map<String, Object>> viewLeaveRequests(String teacherId, String studentId)
            throws ExecutionException, InterruptedException {
        List<Map<String, Object>> leaveRequests = new ArrayList<>();

        // Get schoolId from Teacher_Details to construct the path
        Map<String, Object> teacherDetails = firestore.collection("Teacher_Details")
                .document(teacherId).get().get().getData();
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher ID not found");
        }
        String schoolId = (String) teacherDetails.get("schoolId");

        if (studentId != null && !studentId.isEmpty()) {
            // Fetch leave requests for specific student
            DocumentReference leaveRequestRef = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("Teachers")
                    .document(teacherId)
                    .collection("LeaveRequests")
                    .document(studentId);

            DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();
            if (leaveRequestSnapshot.exists()) {
                Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
                List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        } else {
            // Fetch all leave requests for teacher
            CollectionReference leaveRequestsCollection = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("Teachers")
                    .document(teacherId)
                    .collection("LeaveRequests");

            QuerySnapshot querySnapshot = leaveRequestsCollection.get().get();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> leaveRequestData = document.getData();
                List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        }
        return leaveRequests;
    }

    public List<Map<String, Object>> viewTeacherLeaveRequests(String hodId, String teacherId) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> leaveRequests = new ArrayList<>();

        if (hodId == null || hodId.isEmpty()) {
            throw new IllegalArgumentException("HOD ID cannot be null or empty");
        }

        // Get schoolId from Hod_Details to construct the path
        DocumentSnapshot hodDetailsSnapshot = firestore.collection("Hod_Details").document(hodId).get().get();
        if (!hodDetailsSnapshot.exists()) {
            throw new IllegalArgumentException("HOD ID not found: " + hodId);
        }

        String schoolId = (String) hodDetailsSnapshot.get("schoolId");
        if (schoolId == null || schoolId.isEmpty()) {
            throw new IllegalArgumentException("School ID not found for HOD ID: " + hodId);
        }

        CollectionReference leaveRequestsCollection = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("HOD")
                .document(hodId)
                .collection("LeaveRequests");

        if (teacherId != null && !teacherId.isEmpty()) {
            // Fetch leave requests for specific teacher
            DocumentReference leaveRequestRef = leaveRequestsCollection.document(teacherId);
            DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();

            if (leaveRequestSnapshot.exists()) {
                Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
                if (leaveRequestData != null) {
                    List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                    if (leaveRequestsList != null) {
                        leaveRequests.addAll(leaveRequestsList);
                    }
                }
            }
        } else {
            // Fetch all leave requests for teachers under the HOD
            QuerySnapshot querySnapshot = leaveRequestsCollection.get().get();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> leaveRequestData = document.getData();
                if (leaveRequestData != null) {
                    List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                    if (leaveRequestsList != null) {
                        leaveRequests.addAll(leaveRequestsList);
                    }
                }
            }
        }

        return leaveRequests;
    }

    public List<Map<String, Object>> viewHODLeaveRequests(String hrId, String hodId) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> leaveRequests = new ArrayList<>();

        if (hrId == null || hrId.isEmpty()) {
            throw new IllegalArgumentException("HR ID cannot be null or empty");
        }

        // Get schoolId from Hod_Details to construct the path
        DocumentSnapshot hrDetailsSnapshot = firestore.collection("HR_Details").document(hrId).get().get();
        if (!hrDetailsSnapshot.exists()) {
            throw new IllegalArgumentException("HR ID not found: " + hrId);
        }

        String schoolId = (String) hrDetailsSnapshot.get("schoolId");
        if (schoolId == null || schoolId.isEmpty()) {
            throw new IllegalArgumentException("School ID not found for HR ID: " + hrId);
        }

        CollectionReference leaveRequestsCollection = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("HR")
                .document(hrId)
                .collection("LeaveRequests");

        if (hodId != null && !hodId.isEmpty()) {
            // Fetch leave requests for specific teacher
            DocumentReference leaveRequestRef = leaveRequestsCollection.document(hodId);
            DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();

            if (leaveRequestSnapshot.exists()) {
                Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
                if (leaveRequestData != null) {
                    List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                    if (leaveRequestsList != null) {
                        leaveRequests.addAll(leaveRequestsList);
                    }
                }
            }
        } else {
            // Fetch all leave requests for teachers under the HOD
            QuerySnapshot querySnapshot = leaveRequestsCollection.get().get();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> leaveRequestData = document.getData();
                if (leaveRequestData != null) {
                    List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                    if (leaveRequestsList != null) {
                        leaveRequests.addAll(leaveRequestsList);
                    }
                }
            }
        }

        return leaveRequests;
    }

    public List<Map<String, Object>> viewLeaveRequestsbyStudentId1(String teacherId, String studentId)
            throws ExecutionException, InterruptedException {
        List<Map<String, Object>> leaveRequests = new ArrayList<>();

        // Get schoolId from Teacher_Details to construct the path
        ApiFuture<DocumentSnapshot> teacherDetailsFuture = firestore.collection("Teacher_Details")
                .document(teacherId).get();
        DocumentSnapshot teacherSnapshot = teacherDetailsFuture.get();

        if (!teacherSnapshot.exists()) {
            throw new IllegalArgumentException("Teacher ID not found");
        }

        Map<String, Object> teacherDetails = teacherSnapshot.getData();
        String schoolId = (String) teacherDetails.get("schoolId");

        if (studentId != null && !studentId.isEmpty()) {
            // Fetch leave requests for specific student
            ApiFuture<DocumentSnapshot> leaveRequestFuture = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("Teachers")
                    .document(teacherId)
                    .collection("LeaveRequests")
                    .document(studentId)
                    .get();

            DocumentSnapshot leaveRequestSnapshot = leaveRequestFuture.get();
            if (leaveRequestSnapshot.exists()) {
                Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
                List<Map<String, Object>> leaveRequestsList =
                    (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        } else {
            // Fetch all leave requests for teacher
            ApiFuture<QuerySnapshot> leaveRequestsFuture = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("Teachers")
                    .document(teacherId)
                    .collection("LeaveRequests")
                    .get();

            QuerySnapshot querySnapshot = leaveRequestsFuture.get();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> leaveRequestData = document.getData();
                List<Map<String, Object>> leaveRequestsList =
                    (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        }

        return leaveRequests;
    }

    public List<Map<String, Object>> viewLeaveRequestsbyteacherId(String teacherId, String hodId)
            throws ExecutionException, InterruptedException {
        List<Map<String, Object>> leaveRequests = new ArrayList<>();

        // Get schoolId from Teacher_Details to construct the path
        ApiFuture<DocumentSnapshot> hodDetailsFuture = firestore.collection("Hod_Details")
                .document(hodId).get();
        DocumentSnapshot hodSnapshot = hodDetailsFuture.get();

        if (!hodSnapshot.exists()) {
            throw new IllegalArgumentException("HOD ID not found");
        }

        Map<String, Object> hodDetails = hodSnapshot.getData();
        String schoolId = (String) hodDetails.get("schoolId");

        if (teacherId != null && !teacherId.isEmpty()) {
            // Fetch leave requests for specific student
            ApiFuture<DocumentSnapshot> leaveRequestFuture = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("HOD")
                    .document(hodId)
                    .collection("LeaveRequests")
                    .document(teacherId)
                    .get();

            DocumentSnapshot leaveRequestSnapshot = leaveRequestFuture.get();
            if (leaveRequestSnapshot.exists()) {
                Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
                List<Map<String, Object>> leaveRequestsList =
                    (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        } else {
            // Fetch all leave requests for teacher
            ApiFuture<QuerySnapshot> leaveRequestsFuture = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("HOD")
                    .document(hodId)
                    .collection("LeaveRequests")
                    .get();

            QuerySnapshot querySnapshot = leaveRequestsFuture.get();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> leaveRequestData = document.getData();
                List<Map<String, Object>> leaveRequestsList =
                    (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        }

        return leaveRequests;
    }

    public List<Map<String, Object>> viewLeaveRequestsbyhodId(String hrId, String hodId)
            throws ExecutionException, InterruptedException {
        List<Map<String, Object>> leaveRequests = new ArrayList<>();

        // Get schoolId from Teacher_Details to construct the path
        ApiFuture<DocumentSnapshot> hrDetailsFuture = firestore.collection("HR_Details")
                .document(hrId).get();
        DocumentSnapshot hrSnapshot = hrDetailsFuture.get();

        if (!hrSnapshot.exists()) {
            throw new IllegalArgumentException("HR ID not found");
        }

        Map<String, Object> hrDetails = hrSnapshot.getData();
        String schoolId = (String) hrDetails.get("schoolId");

        if (hodId != null && !hodId.isEmpty()) {
            // Fetch leave requests for specific student
            ApiFuture<DocumentSnapshot> leaveRequestFuture = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("HR")
                    .document(hrId)
                    .collection("LeaveRequests")
                    .document(hodId)
                    .get();

            DocumentSnapshot leaveRequestSnapshot = leaveRequestFuture.get();
            if (leaveRequestSnapshot.exists()) {
                Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
                List<Map<String, Object>> leaveRequestsList =
                    (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        } else {
            // Fetch all leave requests for teacher
            ApiFuture<QuerySnapshot> leaveRequestsFuture = firestore.collection("Schools_Leaves")
                    .document(schoolId)
                    .collection("HR")
                    .document(hrId)
                    .collection("LeaveRequests")
                    .get();

            QuerySnapshot querySnapshot = leaveRequestsFuture.get();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> leaveRequestData = document.getData();
                List<Map<String, Object>> leaveRequestsList =
                    (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
                if (leaveRequestsList != null) {
                    leaveRequests.addAll(leaveRequestsList);
                }
            }
        }

        return leaveRequests;
    }

    public String processLeaveRequest(String teacherId, String studentId, String action)
            throws ExecutionException, InterruptedException {
        // Validate action parameter
        if (!"approve".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action)) {
            throw new IllegalArgumentException("Invalid action. Must be 'approve' or 'reject'");
        }

        // Get schoolId from Teacher_Details
        Map<String, Object> teacherDetails = firestore.collection("Teacher_Details")
                .document(teacherId).get().get().getData();
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher ID not found");
        }
        String schoolId = (String) teacherDetails.get("schoolId");

        DocumentReference leaveRequestRef = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("Teachers")
                .document(teacherId)
                .collection("LeaveRequests")
                .document(studentId);

        DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();
        if (!leaveRequestSnapshot.exists()) {
            throw new IllegalArgumentException("Leave request not found");
        }

        Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
        List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");

        if (leaveRequestsList == null || leaveRequestsList.isEmpty()) {
            throw new IllegalArgumentException("No leave requests found");
        }

        // Process the latest leave request
        Map<String, Object> latestLeaveRequest = leaveRequestsList.get(leaveRequestsList.size() - 1);
        String status = "approve".equalsIgnoreCase(action) ? "approved" : "rejected";
        latestLeaveRequest.put("status", status);

        // Using modern Java time API (recommended)
        latestLeaveRequest.put("processedTimestamp", LocalDateTime.now().toString());

        leaveRequestRef.update("leaveRequests", leaveRequestsList);

        return "Latest leave request " + status + " successfully";
    }

    public String recordTeacherAttendance(String schoolId, String teacherId, Map<String, Object> attendanceDetails)
            throws ExecutionException, InterruptedException {
        // Validate teacherId from Teacher_Details
        Map<String, Object> teacherDetails = firestore.collection("Teacher_Details")
                .document(teacherId).get().get().getData();
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher ID not found");
        }
        String fetchedSchoolId = (String) teacherDetails.get("schoolId");
        if (!fetchedSchoolId.equals(schoolId)) {
            throw new IllegalArgumentException("School ID mismatch");
        }

        // Create attendance document under schoolId/teacherId
        DocumentReference attendanceRef = firestore.collection("teacher_Attendance")
                .document(schoolId)
                .collection("Teachers")
                .document(teacherId)
                .collection("DailyAttendance")
                .document(LocalDate.now().toString());

        DocumentSnapshot attendanceSnapshot = attendanceRef.get().get();

        if (attendanceSnapshot.exists()) {
            // Update existing document
            attendanceRef.update(attendanceDetails);
        } else {
            // Create new document
            attendanceRef.set(attendanceDetails);
        }

        return teacherId;  // Returning teacherId as the reference
    }

    public String HodprocessLeaveRequest(String schoolId, String hodId, String teacherId, String action)
            throws ExecutionException, InterruptedException {
        // Validate action parameter
        if (!"approve".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action)) {
            throw new IllegalArgumentException("Invalid action. Must be 'approve' or 'reject'");
        }

        // Get schoolId from Hod_Details
        Map<String, Object> HodDetails = firestore.collection("Hod_Details")
                .document(hodId).get().get().getData();
        if (HodDetails == null) {
            throw new IllegalArgumentException("HOD ID not found");
        }
        String retrievedSchoolId = (String) HodDetails.get("schoolId");

        // Ensure the retrieved schoolId matches the provided schoolId
        if (!retrievedSchoolId.equals(schoolId)) {
            throw new IllegalArgumentException("School ID mismatch");
        }

        DocumentReference leaveRequestRef = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("HOD")
                .document(hodId)
                .collection("LeaveRequests")
                .document(teacherId);

        DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();

        if (!leaveRequestSnapshot.exists()) {
            throw new IllegalArgumentException("Leave request not found");
        }

        Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
        List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");

        if (leaveRequestsList == null || leaveRequestsList.isEmpty()) {
            throw new IllegalArgumentException("No leave requests found");
        }

        // Process the latest leave request
        Map<String, Object> latestLeaveRequest = leaveRequestsList.get(leaveRequestsList.size() - 1);
        String status = "approve".equalsIgnoreCase(action) ? "approved" : "rejected";
        latestLeaveRequest.put("status", status);

        // Using modern Java time API (recommended)
        latestLeaveRequest.put("processedTimestamp", LocalDateTime.now().toString());

        leaveRequestRef.update("leaveRequests", leaveRequestsList);

        return "Latest leave request " + status + " successfully";
    }
    public String HRprocessLeaveRequest(String schoolId, String hrId, String hodId, String action)
            throws ExecutionException, InterruptedException {
        // Log the input parameters
        System.out.println("schoolId: " + schoolId);
        System.out.println("hrId: " + hrId);
        System.out.println("hodId: " + hodId);
        System.out.println("action: " + action);

        // Validate action parameter
        if (!"approve".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action)) {
            throw new IllegalArgumentException("Invalid action. Must be 'approve' or 'reject'");
        }

        // Get schoolId from HR_Details
        Map<String, Object> HRDetails = firestore.collection("HR_Details")
                .document(hrId).get().get().getData();
        if (HRDetails == null) {
            throw new IllegalArgumentException("HR ID not found");
        }
        String retrievedSchoolId = (String) HRDetails.get("schoolId");

        // Ensure the retrieved schoolId matches the provided schoolId
        if (!retrievedSchoolId.equals(schoolId)) {
            throw new IllegalArgumentException("School ID mismatch");
        }

        DocumentReference leaveRequestRef = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("HR")
                .document(hrId)
                .collection("LeaveRequests")
                .document(hodId);

        DocumentSnapshot leaveRequestSnapshot = leaveRequestRef.get().get();

        if (!leaveRequestSnapshot.exists()) {
            throw new IllegalArgumentException("Leave request not found");
        }

        Map<String, Object> leaveRequestData = leaveRequestSnapshot.getData();
        List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");

        if (leaveRequestsList == null || leaveRequestsList.isEmpty()) {
            throw new IllegalArgumentException("No leave requests found");
        }

        // Process the latest leave request
        Map<String, Object> latestLeaveRequest = leaveRequestsList.get(leaveRequestsList.size() - 1);
        String status = "approve".equalsIgnoreCase(action) ? "approved" : "rejected";
        latestLeaveRequest.put("status", status);

        // Using modern Java time API (recommended)
        latestLeaveRequest.put("processedTimestamp", LocalDateTime.now().toString());

        leaveRequestRef.update("leaveRequests", leaveRequestsList);

        return "Latest leave request " + status + " successfully";
    }

    public List<Map<String, Object>> getAllLeaveRequestsForTeacher(String schoolId, String teacherId)
            throws ExecutionException, InterruptedException {
        List<Map<String, Object>> leaveRequests = new ArrayList<>();

        CollectionReference leaveRequestsCollection = firestore.collection("Schools_Leaves")
                .document(schoolId)
                .collection("Teachers")
                .document(teacherId)
                .collection("LeaveRequests");

        QuerySnapshot querySnapshot = leaveRequestsCollection.get().get();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            Map<String, Object> leaveRequestData = document.getData();
            List<Map<String, Object>> leaveRequestsList = (List<Map<String, Object>>) leaveRequestData.get("leaveRequests");
            if (leaveRequestsList != null) {
                leaveRequests.addAll(leaveRequestsList);
            }
        }
        return leaveRequests;
    }

    public List<Map<String, Object>> viewLeaveRequestsbyStudentId(String teacherId, String studentId) {
        // TODO Auto-generated method stub
        return null;
    }
}
