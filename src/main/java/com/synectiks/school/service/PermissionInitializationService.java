package com.synectiks.school.service;

import java.security.Permission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
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
        List<com.synectiks.school.entity.Permission> permissions = Arrays.asList(
            new com.synectiks.school.entity.Permission("dashboard", "view_dashboard", "Access the main dashboard"),
            new com.synectiks.school.entity.Permission("students", "manage_students", "Add/edit/delete students"),
            new com.synectiks.school.entity.Permission("fee", "manage_fee", "Manage all the details"),
            new com.synectiks.school.entity.Permission("fee", "view_fee", "View fee details")
        );
      System.out.println(permissions);

        for (com.synectiks.school.entity.Permission permission : permissions) {
            Map<String, Object> permissionData = new HashMap<>();
            permissionData.put("category", permission.getCategory());
            permissionData.put("permission", permission.getPermission());
            permissionData.put("description", permission.getDescription());
            firestore.collection("permissions").document(permission.getPermission()).set(permissionData);
        }
    }
}
