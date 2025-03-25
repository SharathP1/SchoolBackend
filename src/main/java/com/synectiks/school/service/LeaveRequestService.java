package com.synectiks.school.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.type.Date;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class LeaveRequestService {

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