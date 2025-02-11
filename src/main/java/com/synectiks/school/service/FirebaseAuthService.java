package com.synectiks.school.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;
import java.util.Arrays;

@Service
public class FirebaseAuthService {

    private final Firestore firestore;
    private final EmailService emailService; // Add EmailService
    private final RoleService roleService;

    public FirebaseAuthService(EmailService emailService,  RoleService roleService) {
        this.firestore = FirestoreClient.getFirestore();
        this.emailService = emailService;
        this.roleService = roleService;
    }


    public Map<String, String> signUp(String schoolName, String email, String location, String password) throws FirebaseAuthException {
        // Step 1: Create a user in Firebase Authentication
        CreateRequest request = new CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        // Step 2: Generate a unique school ID
        String schoolId = UUID.randomUUID().toString();

        // Step 3: Store school details in Firestore
        Map<String, Object> schoolDetails = new HashMap<>();
        schoolDetails.put("schoolId", schoolId);
        schoolDetails.put("schoolName", schoolName);
        schoolDetails.put("email", email);
        schoolDetails.put("location", location);
        schoolDetails.put("uid", uid); // Link to Firebase Auth UID

//        firestore.collection("schools_Auth_details").document(schoolId).set(schoolDetails);
        try {
            firestore.collection("schools_Auth_details").document(schoolId).set(schoolDetails);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write to Firestore: " + e.getMessage());
        }

        // Return a map with schoolId and success message
        Map<String, String> response = new HashMap<>();
        response.put("schoolId", schoolId);
        response.put("message", "School registered successfully!");
        
        createDefaultRoles(schoolId);
        return response;
    }

    public Map<String, String> signIn(String email, String password) throws FirebaseAuthException, InterruptedException, ExecutionException {
        // Firebase SDK does not provide a direct sign-in method for server-side code
        // You need to use Firebase Client SDK for sign-in
        // This method can be used to verify the user's token after they sign in on the client side
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
        String uid = userRecord.getUid();

        // Retrieve schoolId from Firestore
        QuerySnapshot querySnapshot = firestore.collection("schools_Auth_details").whereEqualTo("uid", uid).get().get();
        String schoolId = querySnapshot.getDocuments().get(0).getString("schoolId");

        // Return a map with schoolId and success message
        Map<String, String> response = new HashMap<>();
        response.put("schoolId", schoolId);
        response.put("message", "Sign-in successful!");
        return response;
    }
    

    
    
    
    public Map<String, String> createParentAccounts(String schoolId, List<Map<String, String>> parents) throws FirebaseAuthException {
        Map<String, String> results = new HashMap<>();

        for (Map<String, String> parent : parents) {
            try {
                String studentName = parent.get("studentName");
                String email = parent.get("email");
                String className = parent.get("class");
                String parentName = parent.get("parentName");
                String classTeacher = parent.get("classteacher");

                // Generate a random password
                String password = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

                // Create Firebase user
                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setPassword(password);

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();

                // Store in Firestore
                Map<String, Object> parentDetails = new HashMap<>();
                parentDetails.put("studentName", studentName);
                parentDetails.put("email", email);
                parentDetails.put("class", className);
                parentDetails.put("parentName", parentName);
                parentDetails.put("classteacher", classTeacher);
                parentDetails.put("uid", uid);
                parentDetails.put("password", password); // Optional: Remove if not needed
                parentDetails.put("schoolId", schoolId); // Store schoolId

                firestore.collection("parents_details").document(uid).set(parentDetails);

                // Send email with credentials
                emailService.sendCredentialsEmail(email, password);

                results.put(email, "Account created. Credentials sent to email.");
            } catch (FirebaseAuthException e) {
                results.put(parent.get("email"), "Error: " + e.getMessage());
            } catch (Exception e) {
                results.put(parent.get("email"), "Error: " + e.getMessage());
            }
        }

        return results;
    }

    
    private void createDefaultRoles(String schoolId) {
        // Define default roles and their permissions
        Map<String, List<String>> defaultRoles = new HashMap<>();
        defaultRoles.put("admin", Arrays.asList(
            "view_dashboard", 
            "manage_students", 
            "manage_fee", 
            "view_fee"
        ));
        defaultRoles.put("teacher", Arrays.asList(
            "view_dashboard", 
            "manage_students"
        ));
        defaultRoles.put("parent", Arrays.asList(
            "view_dashboard", 
            "view_fee"
        ));
        defaultRoles.put("staff", Arrays.asList(
            "view_dashboard", 
            "manage_fee"
        ));

        // Create each role
        defaultRoles.forEach((roleName, permissions) -> {
            roleService.createRole(schoolId, roleName, permissions);
        });
    }

    
}