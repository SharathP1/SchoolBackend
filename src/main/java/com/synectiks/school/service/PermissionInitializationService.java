//package com.synectiks.school.service;
//
//import java.security.Permission;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import org.springframework.stereotype.Service;
//
//import com.google.cloud.firestore.Firestore;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthException;
//import com.google.firebase.auth.UserRecord;
//import com.google.firebase.auth.UserRecord.CreateRequest;
//import com.google.firebase.cloud.FirestoreClient;
//
//import jakarta.annotation.PostConstruct;
//
//@Service
//public class PermissionInitializationService {
//
//    private final Firestore firestore;
//
//    public PermissionInitializationService() {
//        this.firestore = FirestoreClient.getFirestore();
//    }
//
//    @PostConstruct
//    public void initializePermissions() {
//        List<com.synectiks.school.entity.Permission> permissions = Arrays.asList(
//            new com.synectiks.school.entity.Permission("dashboard", "view_dashboard", "Access the main dashboard"),
//            new com.synectiks.school.entity.Permission("students", "manage_students", "Add/edit/delete students"),
//            new com.synectiks.school.entity.Permission("fee", "manage_fee", "Manage all the details"),
//            new com.synectiks.school.entity.Permission("fee", "view_fee", "View fee details")
//        );
//      System.out.println(permissions);
//
//        for (com.synectiks.school.entity.Permission permission : permissions) {
//            Map<String, Object> permissionData = new HashMap<>();
//            permissionData.put("category", permission.getCategory());
//            permissionData.put("permission", permission.getPermission());
//            permissionData.put("description", permission.getDescription());
//            firestore.collection("permissions").document(permission.getPermission()).set(permissionData);
//        }
//    }
//}

















package com.synectiks.school.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct;

@Service
public class PermissionInitializationService {

    private final Firestore firestore;

    public PermissionInitializationService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    @PostConstruct
    public void initializePermissions() {
        // List of new permissions
        List<com.synectiks.school.entity.Permission> newPermissions = Arrays.asList(
            new com.synectiks.school.entity.Permission("dashboard", "view_dashboard", "Access the main dashboard"),
            new com.synectiks.school.entity.Permission("students", "manage_students", "Add/edit/delete students"),
            new com.synectiks.school.entity.Permission("fee", "manage_fee", "Manage all the details"),
            new com.synectiks.school.entity.Permission("fee", "view_fee", "View fee details"),
            new com.synectiks.school.entity.Permission("faculty", "create_faculty", "Add new faculty members"),
            new com.synectiks.school.entity.Permission("attendance", "manage_attendance", "Manage student attendance"),
            new com.synectiks.school.entity.Permission("transactions", "view_transactions", "View all transactions"),
            new com.synectiks.school.entity.Permission("lesson", "manage_lesson", "Create and manage lesson plans"),
            new com.synectiks.school.entity.Permission("chat", "access_parent_teacher_chat", "Access parent and teacher chat"),
            new com.synectiks.school.entity.Permission("portfolio", "view_class_portfolio", "View class portfolio"),
            new com.synectiks.school.entity.Permission("documents", "manage_documents", "Manage documents"),
            new com.synectiks.school.entity.Permission("teacher_management", "manage_teachers", "Manage teacher information"),
            new com.synectiks.school.entity.Permission("schedule", "manage_schedule", "Manage class and subject schedule"),
            new com.synectiks.school.entity.Permission("leave", "manage_leave_requests", "Manage leave requests"),
            new com.synectiks.school.entity.Permission("reports", "view_reports", "View reports"),
            new com.synectiks.school.entity.Permission("meetings", "manage_meetings", "Manage meetings"),
            new com.synectiks.school.entity.Permission("hr", "manage_employee_onboarding", "Manage employee onboarding"),
            new com.synectiks.school.entity.Permission("recruitment", "manage_recruitment", "Manage recruitment process"),
            new com.synectiks.school.entity.Permission("payroll", "manage_payroll", "Manage payroll"),
            new com.synectiks.school.entity.Permission("attendance_overview", "view_attendance_overview", "View attendance overview"),
            new com.synectiks.school.entity.Permission("leave_management", "manage_leave", "Manage leave"),
            new com.synectiks.school.entity.Permission("resignations", "manage_resignations", "Manage resignations")
        );

//        // Remove old permissions
//        List<String> oldPermissions = Arrays.asList(
//            "view_dashboard", "manage_students", "manage_fee", "view_fee"
//        );
//
//        for (String permission : oldPermissions) {
//            firestore.collection("permissions").document(permission).delete();
//        }

        // Add new permissions
        for (com.synectiks.school.entity.Permission permission : newPermissions) {
            Map<String, Object> permissionData = new HashMap<>();
            permissionData.put("category", permission.getCategory());
            permissionData.put("permission", permission.getPermission());
            permissionData.put("description", permission.getDescription());
            firestore.collection("permissions").document(permission.getPermission()).set(permissionData);
        }
    }
}

