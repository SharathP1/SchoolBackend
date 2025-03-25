package com.synectiks.school.service;
 
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.Map;
import java.util.concurrent.ExecutionException;
 
@Service
public class UserService {
 
    private final Firestore firestore;
 
    @Autowired
    public UserService() {
        this.firestore = FirestoreClient.getFirestore();
    }
 
    public Map<String, Object> getUserByUserId(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = firestore.collectionGroup("users").whereEqualTo("uid", userId).get();
        QuerySnapshot querySnapshot = query.get();
 
        if (querySnapshot.isEmpty()) {
            throw new RuntimeException("User not found");
        }
 
        QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
        Map<String, Object> userData = document.getData();
        userData.put("userId", document.getId()); // Add the user ID to the data
        return userData;
    }
}