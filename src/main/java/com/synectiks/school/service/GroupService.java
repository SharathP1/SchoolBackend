

package com.synectiks.school.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class GroupService {

    private final Firestore firestore;

    @Autowired
    public GroupService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public void createGroup(String schoolId, String groupName, List<String> permissions) {
        List<String> safePermissions = permissions != null ? permissions : new ArrayList<>();
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("permissions", safePermissions);
        groupData.put("users", new ArrayList<String>());

        System.out.println("Creating group at path: schools/" + schoolId + "/groups/" + groupName);
        System.out.println("Group Permissions: " + safePermissions);

        firestore.collection("schools").document(schoolId).collection("groups").document(groupName).set(groupData);
    }

    public List<Map<String, Object>> getAllGroups(String schoolId) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> groups = new ArrayList<>();

        ApiFuture<QuerySnapshot> query = firestore.collection("schools")
            .document(schoolId)
            .collection("groups")
            .get();

        QuerySnapshot querySnapshot = query.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Map<String, Object> groupData = new HashMap<>();
            groupData.put("groupName", document.getId());
            groupData.put("permissions", document.get("permissions"));
            groupData.put("users", document.get("users"));
            groups.add(groupData);
        }

        return groups;
    }

    public Map<String, Object> getGroup(String schoolId, String groupName) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .get()
                .get();

            if (!document.exists()) {
                throw new RuntimeException("Group not found");
            }

            Map<String, Object> group = new HashMap<>();
            group.put("name", groupName);
            group.put("permissions", document.get("permissions"));
            group.put("users", document.get("users"));
            return group;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching group", e);
        }
    }

    public void updateGroup(String schoolId, String groupName, List<String> permissions) {
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("permissions", permissions != null ? permissions : new ArrayList<>());

        try {
            DocumentSnapshot existingGroup = firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .get()
                .get();

            if (existingGroup.exists()) {
                List<String> existingUsers = (List<String>) existingGroup.get("users");
                if (existingUsers != null) {
                    groupData.put("users", existingUsers);
                }
            }

            firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .set(groupData);
        } catch (Exception e) {
            throw new RuntimeException("Error updating group", e);
        }
    }

    public void deleteGroup(String schoolId, String groupName) {
        try {
            firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .delete();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting group", e);
        }
    }

    public void addUserToGroup(String schoolId, String groupName, String userId) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .get()
                .get();

            if (!document.exists()) {
                throw new RuntimeException("Group not found: " + groupName);
            }

            List<String> users = (List<String>) document.get("users");
            if (users == null) {
                users = new ArrayList<>();
            }

            if (!users.contains(userId)) {
                users.add(userId);
            }

            Map<String, Object> groupData = new HashMap<>();
            groupData.put("users", users);

            List<String> existingPermissions = (List<String>) document.get("permissions");
            if (existingPermissions != null) {
                groupData.put("permissions", existingPermissions);
            }

            firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .set(groupData);

            System.out.println("Added user " + userId + " to group " + groupName);
        } catch (Exception e) {
            System.err.println("Error adding user to group: " + e.getMessage());
            throw new RuntimeException("Error adding user to group", e);
        }
    }

    public void addUsersToGroup(String schoolId, String groupName, List<String> userIds) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .get()
                .get();

            if (!document.exists()) {
                throw new RuntimeException("Group not found: " + groupName);
            }

            List<String> users = (List<String>) document.get("users");
            if (users == null) {
                users = new ArrayList<>();
            }

            for (String userId : userIds) {
                if (!users.contains(userId)) {
                    users.add(userId);
                }
            }

            Map<String, Object> groupData = new HashMap<>();
            groupData.put("users", users);

            List<String> existingPermissions = (List<String>) document.get("permissions");
            if (existingPermissions != null) {
                groupData.put("permissions", existingPermissions);
            }

            firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .set(groupData);

            System.out.println("Added " + userIds.size() + " users to group " + groupName);
        } catch (Exception e) {
            System.err.println("Error adding users to group: " + e.getMessage());
            throw new RuntimeException("Error adding users to group", e);
        }
    }

    public void removeUserFromGroup(String schoolId, String groupName, String userId) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .get()
                .get();

            if (!document.exists()) {
                throw new RuntimeException("Group not found: " + groupName);
            }

            List<String> users = (List<String>) document.get("users");
            if (users == null) {
                users = new ArrayList<>();
            }

            users.remove(userId);

            Map<String, Object> groupData = new HashMap<>();
            groupData.put("users", users);

            List<String> existingPermissions = (List<String>) document.get("permissions");
            if (existingPermissions != null) {
                groupData.put("permissions", existingPermissions);
            }

            firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .set(groupData);

            System.out.println("Removed user " + userId + " from group " + groupName);
        } catch (Exception e) {
            System.err.println("Error removing user from group: " + e.getMessage());
            throw new RuntimeException("Error removing user from group", e);
        }
    }

    public boolean isGroupCreated(String schoolId, String groupName) {
        try {
            DocumentSnapshot document = firestore.collection("schools")
                .document(schoolId)
                .collection("groups")
                .document(groupName)
                .get()
                .get();

            boolean exists = document.exists();
            System.out.println("Group " + groupName + " exists: " + exists);
            return exists;
        } catch (Exception e) {
            System.err.println("Error checking group existence: " + e.getMessage());
            return false;
        }
    }
    
    public List<Map<String, Object>> getGroupsByUserId(String schoolId, String userId) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> groups = new ArrayList<>();

        ApiFuture<QuerySnapshot> query = firestore.collection("schools")
            .document(schoolId)
            .collection("groups")
            .get();

        QuerySnapshot querySnapshot = query.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            List<String> users = (List<String>) document.get("users");
            if (users != null && users.contains(userId)) {
                Map<String, Object> groupData = new HashMap<>();
                groupData.put("groupName", document.getId());
                groupData.put("permissions", document.get("permissions"));
                groupData.put("users", users);
                groups.add(groupData);
            }
        }

        return groups;
    }
}
