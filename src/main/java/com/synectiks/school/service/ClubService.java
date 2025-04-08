package com.synectiks.school.service;
 
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
 
@Service
public class ClubService {
 
    private final Firestore firestore;
 
    @Autowired
    public ClubService() {
        this.firestore = FirestoreClient.getFirestore();
    }
 
    public String createClub(String schoolId, String clubName, String createdBy) {
        String clubId = UUID.randomUUID().toString();  // Generate unique ID for the club
        Map<String, Object> clubData = new HashMap<>();
        clubData.put("clubName", clubName);
        clubData.put("clubId", clubId);
        clubData.put("members", new ArrayList<String>());
        clubData.put("score", 0L); // Initialize score to 0
        clubData.put("createdBy", createdBy); // Teacher who created the club
 
        System.out.println("Creating club at path: schools/" + schoolId + "/clubs/" + clubId);
 
 
        try {
            firestore.collection("schools").document(schoolId).collection("clubs").document(clubId).set(clubData).get();  // Wait for write to complete
            return clubId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create club: " + clubName, e);
        }
    }
 
    public List<Map<String, Object>> getAllClubs(String schoolId) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> clubs = new ArrayList<>();
 
        ApiFuture<QuerySnapshot> query = firestore.collection("schools")
                .document(schoolId)
                .collection("clubs")
                .get();
 
        QuerySnapshot querySnapshot = query.get();
 
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Map<String, Object> clubData = new HashMap<>();
            clubData.putAll(document.getData());
            clubData.put("clubId", document.getId()); // Add the club ID
            clubs.add(clubData);
        }
 
        return clubs;
    }
 
 
    public Map<String, Object> getClub(String schoolId, String clubId) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .get()
                    .get();
 
            if (!document.exists()) {
                throw new RuntimeException("Club not found with ID: " + clubId);
            }
 
            Map<String, Object> clubData = new HashMap<>();
            clubData.putAll(document.getData());
            clubData.put("clubId", document.getId()); // Add the club ID
            return clubData;
 
        } catch (Exception e) {
            throw new RuntimeException("Error fetching club with ID: " + clubId, e);
        }
    }
 
    public void updateClub(String schoolId, String clubId, String clubName) {
        try {
            DocumentReference clubRef = firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId);
 
            ApiFuture<DocumentSnapshot> future = clubRef.get();
            DocumentSnapshot document = future.get();
 
            if (!document.exists()) {
                throw new RuntimeException("Club not found with ID: " + clubId);
            }
 
            Map<String, Object> updates = new HashMap<>();
            updates.put("clubName", clubName);
 
            clubRef.update(updates);
 
        } catch (Exception e) {
            throw new RuntimeException("Error updating club with ID: " + clubId, e);
        }
    }
 
 
 
    public void deleteClub(String schoolId, String clubId) {
        try {
            firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .delete();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting club with ID: " + clubId, e);
        }
    }
 
   public void addStudentsToClub(String schoolId, String clubId, List<String> studentIds) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .get()
                    .get();
 
            if (!document.exists()) {
                throw new RuntimeException("Club not found with ID: " + clubId);
            }
 
            List<String> members = (List<String>) document.get("members");
            if (members == null) {
                members = new ArrayList<>();
            }
 
            for (String studentId : studentIds) {
                if (!members.contains(studentId)) {
                    members.add(studentId);
                }
            }
 
            Map<String, Object> clubData = new HashMap<>();
            clubData.put("members", members);
 
            // Keep existing fields in the club document
            String clubName = (String) document.get("clubName");
            Long score = (Long) document.get("score");
            String createdBy = (String) document.get("createdBy");
 
            if (clubName != null) {
                clubData.put("clubName", clubName);
            }
            if (score != null) {
                clubData.put("score", score);
            }
            if (createdBy != null) {
                clubData.put("createdBy", createdBy);
            }
 
 
            firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .set(clubData); // Use set to update the document with new members list and existing details
 
            System.out.println("Added students " + studentIds + " to club " + clubId);
        } catch (Exception e) {
            System.err.println("Error adding students to club: " + e.getMessage());
            throw new RuntimeException("Error adding students to club", e);
        }
    }
 
 
    public void removeStudentFromClub(String schoolId, String clubId, String studentId) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .get()
                    .get();
 
            if (!document.exists()) {
                throw new RuntimeException("Club not found with ID: " + clubId);
            }
 
            List<String> members = (List<String>) document.get("members");
            if (members == null) {
                members = new ArrayList<>();
            }
 
            members.remove(studentId);
 
            Map<String, Object> clubData = new HashMap<>();
            clubData.put("members", members);
 
            // Keep existing fields in the club document
            String clubName = (String) document.get("clubName");
            Long score = (Long) document.get("score");
            String createdBy = (String) document.get("createdBy");
 
            if (clubName != null) {
                clubData.put("clubName", clubName);
            }
            if (score != null) {
                clubData.put("score", score);
            }
            if (createdBy != null) {
                clubData.put("createdBy", createdBy);
            }
 
 
 
            firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .set(clubData); // Use set to update the document
 
            System.out.println("Removed student " + studentId + " from club " + clubId);
        } catch (Exception e) {
            System.err.println("Error removing student from club: " + e.getMessage());
            throw new RuntimeException("Error removing student from club", e);
        }
    }
 
 
    public void updateClubScore(String schoolId, String clubId, Long score) {
        try {
            DocumentReference clubRef = firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId);
 
            ApiFuture<DocumentSnapshot> future = clubRef.get();
            DocumentSnapshot document = future.get();
 
            if (!document.exists()) {
                throw new RuntimeException("Club not found with ID: " + clubId);
            }
 
            Map<String, Object> updates = new HashMap<>();
            updates.put("score", score);
 
            clubRef.update(updates);
 
        } catch (Exception e) {
            throw new RuntimeException("Error updating club score for ID: " + clubId, e);
        }
    }
 
    public Long getClubScore(String schoolId, String clubId) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .get()
                    .get();
 
            if (!document.exists()) {
                throw new RuntimeException("Club not found with ID: " + clubId);
            }
 
            Long score = document.getLong("score");
            return score != null ? score : 0L;  // Return 0 if score is null
 
        } catch (Exception e) {
            throw new RuntimeException("Error fetching club score with ID: " + clubId, e);
        }
    }
 
 
    public List<Map<String, Object>> getClubsByStudentId(String schoolId, String studentId) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> clubs = new ArrayList<>();
 
        ApiFuture<QuerySnapshot> query = firestore.collection("schools")
                .document(schoolId)
                .collection("clubs")
                .get();
 
        QuerySnapshot querySnapshot = query.get();
 
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            List<String> members = (List<String>) document.get("members");
            if (members != null && members.contains(studentId)) {
                Map<String, Object> clubData = new HashMap<>();
                 clubData.putAll(document.getData());
                clubData.put("clubId", document.getId()); // Add the club ID
 
                clubs.add(clubData);
            }
        }
 
        return clubs;
    }
 
     public boolean isClubCreated(String schoolId, String clubId) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                    .document(schoolId)
                    .collection("clubs")
                    .document(clubId)
                    .get()
                    .get();
 
            boolean exists = document.exists();
            System.out.println("Club " + clubId + " exists: " + exists);
 
            if(exists) {
                System.out.println("Club Document Data: " + document.getData()); // ADD THIS LINE
            }
 
            return exists;
        } catch (Exception e) {
            System.err.println("Error checking club existence: " + e.getMessage());
            return false;
        }
    }
 
 
}