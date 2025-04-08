package com.synectiks.school.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class HODService {

    private final Firestore firestore;

    public HODService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    // Add HOD details
    public void addHod(Map<String, Object> hodDetails, String Department, String schoolId, String uid) throws Exception {
        if (hodDetails == null) {
            throw new IllegalArgumentException("HOD details cannot be null");
        }

        CollectionReference hodCollection = firestore.collection("Hod_Details");
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

        // Extract hodId from the user document
        String hodId = userSnapshot.getString("hodId");
        if (hodId == null) {
            throw new IllegalArgumentException("No hodId found in user document with UID " + uid);
        }

        // Extract relevant fields from the user document
        Map<String, Object> userData = userSnapshot.getData();
        if (userData != null) {
            // Add user data to hodDetails (only if not already set)
            if (!hodDetails.containsKey("email") && userData.containsKey("email")) {
                hodDetails.put("email", userData.get("email"));
            }
            if (!hodDetails.containsKey("name") && userData.containsKey("name")) {
                hodDetails.put("name", userData.get("name"));
            }
            // Add more fields as needed based on your users collection structure
        }

        // Add extracted hodId and other reference fields to hod details
        hodDetails.put("id", hodId);
        hodDetails.put("Department", Department);
        hodDetails.put("schoolId", schoolId);

        DocumentReference hodDocument = hodCollection.document(hodId);
        ApiFuture<WriteResult> insertingDataInDocument = hodDocument.set(hodDetails);
    }


    // Get all HODs for a school
    public List<Map<String, Object>> getAllHodDetails(String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference hodDetailsTable = firestore.collection("Hod_Details");
        ApiFuture<QuerySnapshot> querySnapshot = hodDetailsTable.whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> hodDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            hodDetailsList.add(details.getData());
        }
        return hodDetailsList;
    }

    // Get HOD details by ID
    public Map<String, Object> getHodDetailsById(String id, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference hodDetailsTable = firestore.collection("Hod_Details");
        ApiFuture<QuerySnapshot> querySnapshot = hodDetailsTable.whereEqualTo("id", id).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("HOD not found");
        }
        return documents.get(0).getData();
    }

    // Get HOD details by department
    public List<Map<String, Object>> getHodDetailsByDepartment(String department, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference hodDetailsTable = firestore.collection("Hod_Details");
        ApiFuture<QuerySnapshot> querySnapshot = hodDetailsTable.whereEqualTo("department", department).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> hodDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            hodDetailsList.add(details.getData());
        }
        return hodDetailsList;
    }

    // Get all teachers under an HOD by department
    public List<Map<String, Object>> getTeachersByDepartment(String department, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("department", department).whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }
        return teacherDetailsList;
    }

    // Approve or reject a lesson plan
    public void approveOrRejectLessonPlan(String lessonPlanId, String teacherId, String schoolId, String hodId, String status) 
            throws InterruptedException, ExecutionException {
        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");

        // Query using lessonPlanId, teacherId, and schoolId
        ApiFuture<QuerySnapshot> querySnapshot = lessonPlanCollection
                .whereEqualTo("id", lessonPlanId)
                .whereEqualTo("teacherId", teacherId)
                .whereEqualTo("schoolId", schoolId)
                .get();
        
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Lesson plan not found with ID: " + lessonPlanId + 
                                     " for teacher: " + teacherId + 
                                     " in school: " + schoolId);
        }

        // Validate status
        if (!"approved".equalsIgnoreCase(status) && !"rejected".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Status must be either 'approved' or 'rejected'");
        }

        DocumentReference lessonPlanDoc = documents.get(0).getReference();
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status.toLowerCase()); // Store as lowercase
        updates.put("approvedBy", hodId);
        updates.put("updatedAt", new Date());

        ApiFuture<WriteResult> updateResult = lessonPlanDoc.update(updates);
        updateResult.get();
    }

    // Get all lesson plans for a department (pending approval)
    public List<Map<String, Object>> getLessonPlansForApproval(String department, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");

        // Get teachers in the department
        ApiFuture<QuerySnapshot> teacherQuery = teacherDetailsTable
                .whereEqualTo("department", department)
                .whereEqualTo("schoolId", schoolId)
                .get();
        List<QueryDocumentSnapshot> teacherDocs = teacherQuery.get().getDocuments();
        List<String> teacherIds = new ArrayList<>();
        for (QueryDocumentSnapshot teacher : teacherDocs) {
            teacherIds.add((String) teacher.getData().get("id"));
        }

        // Get lesson plans for those teachers
        ApiFuture<QuerySnapshot> querySnapshot = lessonPlanCollection
                .whereIn("teacherId", teacherIds)
                .whereEqualTo("schoolId", schoolId)
                .whereEqualTo("status", "pending")
                .get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> lessonPlans = new ArrayList<>();

        SimpleDateFormat istDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        istDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> lessonPlan = document.getData();
            Timestamp createdAt = (Timestamp) lessonPlan.get("createdAt");
            Timestamp updatedAt = (Timestamp) lessonPlan.get("updatedAt");

            if (createdAt != null) {
                lessonPlan.put("createdAt", istDateFormat.format(new Date(createdAt.getSeconds() * 1000)));
            }
            if (updatedAt != null) {
                lessonPlan.put("updatedAt", istDateFormat.format(new Date(updatedAt.getSeconds() * 1000)));
            }
            lessonPlans.add(lessonPlan);
        }
        return lessonPlans;
    }
    
    public List<Map<String, Object>> getLessonPlan(String hodId, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");

        // Query the collection to find documents with matching 'approvedBy' and 'schoolId'
        ApiFuture<QuerySnapshot> querySnapshot = lessonPlanCollection
            .whereEqualTo("hodId", hodId) // Filter by dynamic approvedBy value
            .whereEqualTo("schoolId", schoolId)
            .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Lesson plans not found for approvedBy: " + hodId + " and school ID: " + schoolId);
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
}