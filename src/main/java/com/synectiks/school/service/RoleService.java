package com.synectiks.school.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
@Service
public class RoleService {

    private final Firestore firestore;

    public RoleService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public void createRole(String schoolId, String roleName, List<String> permissions) {
        Map<String, Object> roleData = new HashMap<>();
        roleData.put("permissions", permissions);
        firestore.collection("schools").document(schoolId).collection("roles").document(roleName).set(roleData);
    }

    public void updateRole(String schoolId, String roleName, List<String> permissions) {
        Map<String, Object> roleData = new HashMap<>();
        roleData.put("permissions", permissions);
        firestore.collection("schools").document(schoolId).collection("roles").document(roleName).set(roleData);
    }

    public void deleteRole(String schoolId, String roleName) {
        firestore.collection("schools").document(schoolId).collection("roles").document(roleName).delete();
    }
    
    public List<Map<String, Object>> getAllRoles(String schoolId) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> roles = new ArrayList<>();
        
        // Reference to the roles collection for the specific school
        ApiFuture<QuerySnapshot> query = firestore.collection("schools")
            .document(schoolId)
            .collection("roles")
            .get();

        QuerySnapshot querySnapshot = query.get();
        
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Map<String, Object> roleData = new HashMap<>();
            roleData.put("roleName", document.getId());
            roleData.put("permissions", document.get("permissions"));
            roles.add(roleData);
        }
        
        return roles;
    }

    public Map<String, Object> getRole(String schoolId, String roleName) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                .document(schoolId)
                .collection("roles")
                .document(roleName)
                .get()
                .get();

            if (!document.exists()) {
                throw new RuntimeException("Role not found");
            }

            Map<String, Object> role = new HashMap<>();
            role.put("name", roleName);
            role.put("permissions", document.get("permissions"));
            return role;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching role", e);
        }
    }
    
    
}