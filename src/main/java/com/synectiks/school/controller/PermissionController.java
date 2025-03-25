package com.synectiks.school.controller;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.school.service.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final Firestore firestore;

    public PermissionController() {
        this.firestore = FirestoreClient.getFirestore();
    }

    @GetMapping
    public ResponseEntity<List<com.synectiks.school.entity.Permission>> getPermissions() {
        List<com.synectiks.school.entity.Permission> permissions = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = firestore.collection("permissions").get();
        try {
            for (QueryDocumentSnapshot document : query.get().getDocuments()) {
                com.synectiks.school.entity.Permission permission = new com.synectiks.school.entity.Permission();
                permission.setCategory(document.getString("category"));
                permission.setPermission(document.getString("permission"));
                permission.setDescription(document.getString("description"));
                permissions.add(permission);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(permissions);
    }
}
