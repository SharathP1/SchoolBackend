package com.synectiks.school.service;
 
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
 
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
 
@Service
public class FirebaseAuthService {
 
    private final Firestore firestore;
    private final EmailService emailService;
    private final GroupService groupService;
 
    public FirebaseAuthService(EmailService emailService, GroupService groupService) {
        this.firestore = FirestoreClient.getFirestore();
        this.emailService = emailService;
        this.groupService = groupService;
    }
 
    public Map<String, String> signUp(String schoolName, String email, String location, String password) throws FirebaseAuthException {
        CreateRequest request = new CreateRequest()
                .setEmail(email)
                .setPassword(password);
 
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();
 
        String schoolId = UUID.randomUUID().toString();
 
        Map<String, Object> schoolDetails = new HashMap<>();
        schoolDetails.put("schoolId", schoolId);
        schoolDetails.put("schoolName", schoolName);
        schoolDetails.put("email", email);
        schoolDetails.put("location", location);
        schoolDetails.put("uid", uid);
 
        try {
            firestore.collection("schools").document(schoolId).set(schoolDetails);
 
            // Store admin user in the users subcollection
            Map<String, Object> adminDetails = new HashMap<>();
            adminDetails.put("groups", "admin");
            adminDetails.put("email", email);
            adminDetails.put("uid", uid);
            adminDetails.put("schoolId", schoolId);
 
            firestore.collection("schools").document(schoolId).collection("users").document(uid).set(adminDetails);
        } catch (Exception e) {
            FirebaseAuth.getInstance().deleteUser(uid);  // Rollback user creation
            throw new RuntimeException("Failed to write to Firestore: " + e.getMessage());
        }
 
        Map<String, String> response = new HashMap<>();
        response.put("schoolId", schoolId);
        response.put("message", "School registered successfully!");
 
        createDefaultGroups(schoolId, uid);
        return response;
    }
 
 
    public Map<String, String> signIn(String email, String password) throws FirebaseAuthException {
        // Firebase SDK does not provide a direct sign-in method for server-side code
        // You need to use Firebase Client SDK for sign-in
        // This method can be used to verify the user's token after they sign in on the client side
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
        String uid = userRecord.getUid();
 
        // Retrieve schoolId from Firestore
        //QuerySnapshot querySnapshot = firestore.collection("schools_Auth_details").whereEqualTo("uid", uid).get().get();
       // String schoolId = querySnapshot.getDocuments().get(0).getString("schoolId");
        // TODO: Retrieve schoolId from Firestore, potentially from the user document itself.
        // Return a map with schoolId and success message
        Map<String, String> response = new HashMap<>();
//        response.put("schoolId", schoolId);
        response.put("message", "Sign-in successful!");
        return response;
    }
 
 
    
    public Map<String, String> createParentAccounts(String schoolId, List<Map<String, String>> parents) throws FirebaseAuthException {
        Map<String, String> results = new HashMap<>();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
 
        for (Map<String, String> parent : parents) {
            try {
                String studentName = parent.get("studentName");
                String email = parent.get("email");
                String className = parent.get("class");
                String parentName = parent.get("parentName");
                String classTeacher = parent.get("classteacher");
 
                if (email == null || !emailPattern.matcher(email).matches()) {
                    results.put(email, "Error: Invalid email format");
                    continue;
                }
 
                String password = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
 
                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setPassword(password);
 
                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();
 
                Map<String, Object> parentDetails = new HashMap<>();
                parentDetails.put("role", "parent");
                parentDetails.put("studentName", studentName);
                parentDetails.put("email", email);
                parentDetails.put("class", className);
                parentDetails.put("parentName", parentName);
                parentDetails.put("classteacher", classTeacher);
                parentDetails.put("uid", uid);
                parentDetails.put("password", password); // Don't store password.  Send a reset link.
                parentDetails.put("schoolId", schoolId);
 
                firestore.collection("schools").document(schoolId).collection("users").document(uid).set(parentDetails);
 
                // Send credentials email
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
 
 
 
    private void createDefaultGroups(String schoolId, String adminUid) {
        // Define default groups with comprehensive permissions
        Map<String, List<String>> defaultGroups = new HashMap<>();
        defaultGroups.put("admin", Arrays.asList(
                "view_dashboard",
                "manage_students",
                "manage_fee",
                "view_fee",
                "create_faculty",
                "manage_attendance",
                "view_transactions",
                "manage_lesson",
                "access_parent_teacher_chat",
                "view_class_portfolio",
                "manage_documents",
                "manage_teachers",
                "manage_schedule",
                "manage_leave_requests",
                "view_reports",
                "manage_meetings",
                "manage_employee_onboarding",
                "manage_recruitment",
                "manage_payroll",
                "view_attendance_overview",
                "manage_leave",
                "manage_resignations",
                "create_groups",
                "manage_users",
                "edit_school_settings"
        ));
        defaultGroups.put("minimal", Arrays.asList(
                "view_dashboard"
        ));
 
 
        // Create each group and add the admin user to them
     
        defaultGroups.forEach((groupName, permissions) -> {
            try {
                groupService.createGroup(schoolId, groupName, permissions);
                System.out.println("Group created: " + groupName);
 
                boolean groupCreated = false;
                int maxRetries = 3;
                for (int i = 0; i < maxRetries; i++) {
                    try {
                        Thread.sleep(200); // Short delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
 
                    if (groupService.isGroupCreated(schoolId, groupName)) {
                        groupCreated = true;
                        break;
                    }
                }
 
                if (!groupCreated) {
                    throw new RuntimeException("Failed to verify group creation after multiple retries: " + groupName);
                }
 
                if ("admin".equals(groupName)) {
                    groupService.addUserToGroup(schoolId, groupName, adminUid);
                    System.out.println("Admin user added to group: " + groupName);
                }
            } catch (Exception e) {
                System.err.println("Error processing group: " + groupName);
                e.printStackTrace();
                throw new RuntimeException("Failed to create or process group: " + groupName, e);
            }
        });
    }
}
 