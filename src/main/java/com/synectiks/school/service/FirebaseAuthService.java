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
 
 
    
    public Map<String, Map<String, String>> createParentAccounts(String schoolId, List<Map<String, String>> parents) throws FirebaseAuthException {
        Map<String, Map<String, String>> results = new HashMap<>();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        for (Map<String, String> parent : parents) {
            try {
                String studentName = parent.get("studentName");
                String email = parent.get("email");
                String className = parent.get("class");
                String parentName = parent.get("parentName");
                String classTeacher = parent.get("classteacher");

                if (email == null || !emailPattern.matcher(email).matches()) {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("error", "Invalid email format");
                    results.put(email, errorDetails);
                    continue;
                }

                String password = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                // Generate SID in UUID format (e.g., 0849f38d-4596-4c50-8f9a-d4570b0199e3)
                String sid = UUID.randomUUID().toString();

                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setPassword(password);

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();

                Map<String, Object> parentDetails = new HashMap<>();
                parentDetails.put("role", "Parent");
                parentDetails.put("studentName", studentName);
                parentDetails.put("email", email);
                parentDetails.put("class", className);
                parentDetails.put("parentName", parentName);
                parentDetails.put("classteacher", classTeacher);
                parentDetails.put("uid", uid);
                parentDetails.put("password", password); // Don't store password. Send a reset link.
                parentDetails.put("schoolId", schoolId);
                parentDetails.put("sid", sid); // Added SID in UUID format

                firestore.collection("schools").document(schoolId).collection("users").document(uid).set(parentDetails);

                // Send credentials email with SID and schoolId included
                emailService.sendCredentialsEmail(email, password, sid, schoolId);

                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "Account created");
                successDetails.put("sid", sid);
                successDetails.put("schoolId", schoolId);
                successDetails.put("uid", uid);
                successDetails.put("message", "Credentials sent to email.");
                results.put(email, successDetails);

            } catch (FirebaseAuthException e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(parent.get("email"), errorDetails);
            } catch (Exception e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(parent.get("email"), errorDetails);
            }
        }

        return results;
    }

    
    public Map<String, Map<String, String>> createTeacherAccounts(String schoolId, List<Map<String, String>> teachers)
            throws FirebaseAuthException {
        Map<String, Map<String, String>> results = new HashMap<>();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        for (Map<String, String> teacher : teachers) {
            try {
                String email = teacher.get("email");
                String className = teacher.get("class");
                String TeacherName = teacher.get("TeacherName");
                String password = teacher.get("password");
                String teacherId = teacher.get("teacherId"); // TID already generated in controller

                if (email == null || !emailPattern.matcher(email).matches()) {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("error", "Invalid email format");
                    results.put(email, errorDetails);
                    continue;
                }

                String teacherPassword = (password != null && !password.isEmpty())
                    ? password
                    : UUID.randomUUID().toString().replace("-", "").substring(0, 8);

                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setPassword(teacherPassword);

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();

                Map<String, Object> teacherDetails = new HashMap<>();
                teacherDetails.put("role", "Teacher");
                teacherDetails.put("email", email);
                teacherDetails.put("class", className);
                teacherDetails.put("TeacherName", TeacherName);
                teacherDetails.put("teacherPassword", teacherPassword);
                teacherDetails.put("uid", uid);
                teacherDetails.put("schoolId", schoolId);
                teacherDetails.put("teacherId", teacherId);

                firestore.collection("schools").document(schoolId)
                        .collection("users").document(uid)
                        .set(teacherDetails);

                emailService.sendCredentialsEmail(email, teacherPassword, teacherId);

                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "Account created");
                successDetails.put("teacherId", teacherId);
                successDetails.put("uid", uid);
                successDetails.put("message", "Credentials sent to email.");
                results.put(email, successDetails);

            } catch (FirebaseAuthException e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(teacher.get("email"), errorDetails);
            } catch (Exception e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(teacher.get("email"), errorDetails);
            }
        }

        return results;
    }

    
    public Map<String, Map<String, String>> createHodAccounts(String schoolId, List<Map<String, String>> hods) throws FirebaseAuthException {
        Map<String, Map<String, String>> results = new HashMap<>();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        for (Map<String, String> hod : hods) {
            try {
                String HODName = hod.get("HODName");
                String Department = hod.get("Department");
                String email = hod.get("email");

                if (email == null || !emailPattern.matcher(email).matches()) {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("error", "Invalid email format");
                    results.put(email, errorDetails);
                    continue;
                }

                // Generate random password (8 characters)
                String password = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                // Generate HODId in UUID format
                String hodId = UUID.randomUUID().toString();

                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setPassword(password);

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();

                Map<String, Object> hodDetails = new HashMap<>();
                hodDetails.put("role", "HOD");
                hodDetails.put("HODName", HODName);
                hodDetails.put("Department", Department);
                hodDetails.put("email", email);
                hodDetails.put("uid", uid);
                hodDetails.put("password", password); // Note: Should not store password long-term
                hodDetails.put("schoolId", schoolId);
                hodDetails.put("hodId", hodId);

                firestore.collection("schools")
                        .document(schoolId)
                        .collection("users")
                        .document(uid)
                        .set(hodDetails);

                // Send credentials email with HODId and schoolId
                emailService.sendCredentialsEmail(email, password, hodId, schoolId);

                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "Account created");
                successDetails.put("hodId", hodId);
                successDetails.put("schoolId", schoolId);
                successDetails.put("uid", uid);
                successDetails.put("message", "Credentials sent to email.");
                results.put(email, successDetails);

            } catch (FirebaseAuthException e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(hod.get("email"), errorDetails);
            } catch (Exception e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(hod.get("email"), errorDetails);
            }
        }

        return results;
    }

    
    public Map<String, Map<String, String>> createHRAccounts(String schoolId, List<Map<String, String>> hrs) throws FirebaseAuthException {
        Map<String, Map<String, String>> results = new HashMap<>();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        for (Map<String, String> hr : hrs) {
            try {
                String hrName = hr.get("HRName");
                String email = hr.get("email");

                if (email == null || !emailPattern.matcher(email).matches()) {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("error", "Invalid email format");
                    results.put(email, errorDetails);
                    continue;
                }

                // Generate random password (8 characters)
                String password = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                // Generate hrId in UUID format
                String hrId = UUID.randomUUID().toString();

                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setPassword(password);

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();

                Map<String, Object> hrDetails = new HashMap<>();
                hrDetails.put("role", "HR");
                hrDetails.put("HRName", hrName);
                hrDetails.put("email", email);
                hrDetails.put("uid", uid);
                hrDetails.put("password", password); // Note: Should not store password long-term
                hrDetails.put("schoolId", schoolId);
                hrDetails.put("hrId", hrId);

                firestore.collection("schools")
                        .document(schoolId)
                        .collection("users")
                        .document(uid)
                        .set(hrDetails);

                // Send credentials email with hrId and schoolId
                emailService.sendCredentialsEmail(email, password, hrId, schoolId);

                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "Account created");
                successDetails.put("hrId", hrId);
                successDetails.put("schoolId", schoolId);
                successDetails.put("uid", uid);
                successDetails.put("message", "Credentials sent to email.");
                results.put(email, successDetails);

            } catch (FirebaseAuthException e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(hr.get("email"), errorDetails);
            } catch (Exception e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(hr.get("email"), errorDetails);
            }
        }

        return results;
    }

    
    public Map<String, Map<String, String>> createCoordinatorAccounts(String schoolId, List<Map<String, String>> coordinators) throws FirebaseAuthException {
        Map<String, Map<String, String>> results = new HashMap<>();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        for (Map<String, String> coordinator : coordinators) {
            try {
                String coordinatorName = coordinator.get("coordinatorName");
                String email = coordinator.get("email");

                if (email == null || !emailPattern.matcher(email).matches()) {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("error", "Invalid email format");
                    results.put(email, errorDetails);
                    continue;
                }

                // Generate random password (8 characters)
                String password = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                // Generate CoordinatorId in UUID format
                String coordinatorId = UUID.randomUUID().toString();

                CreateRequest request = new CreateRequest()
                        .setEmail(email)
                        .setPassword(password);

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();

                Map<String, Object> coordinatorDetails = new HashMap<>();
                coordinatorDetails.put("role", "Coordinator");
                coordinatorDetails.put("coordinatorName", coordinatorName);
                coordinatorDetails.put("email", email);
                coordinatorDetails.put("uid", uid);
                coordinatorDetails.put("password", password); // Note: Should not store password long-term
                coordinatorDetails.put("schoolId", schoolId);
                coordinatorDetails.put("coordinatorId", coordinatorId);

                firestore.collection("schools")
                        .document(schoolId)
                        .collection("users")
                        .document(uid)
                        .set(coordinatorDetails);

                // Send credentials email with CoordinatorId and schoolId
                emailService.sendCredentialsEmail(email, password, coordinatorId, schoolId);

                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "Account created");
                successDetails.put("coordinatorId", coordinatorId);
                successDetails.put("schoolId", schoolId);
                successDetails.put("uid", uid);
                successDetails.put("message", "Credentials sent to email.");
                results.put(email, successDetails);

            } catch (FirebaseAuthException e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(coordinator.get("email"), errorDetails);
            } catch (Exception e) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("error", e.getMessage());
                results.put(coordinator.get("email"), errorDetails);
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
 