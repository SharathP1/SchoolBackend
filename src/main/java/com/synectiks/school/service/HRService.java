package com.synectiks.school.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service

public class HRService {
	
	private final Firestore firestore;
	
	public HRService() {
        this.firestore = FirestoreClient.getFirestore();
    }
	
	public void addHr(Map<String, Object> hrDetails, String schoolId, String uid) throws Exception {
        if (hrDetails == null) {
            throw new IllegalArgumentException("HOD details cannot be null");
        }

        CollectionReference hodCollection = firestore.collection("HR_Details");
        CollectionReference schoolsCollection = firestore.collection("schools");

        // Reference to users subcollection under the specific school
        DocumentReference userDoc = schoolsCollection
            .document(schoolId)
            .collection("users")
            .document(uid);

        // Fetch user data from Firestore
        ApiFuture<DocumentSnapshot> userFuture = userDoc.get();
        DocumentSnapshot userSnapshot = userFuture.get();

        if (!userSnapshot.exists()) {
            throw new IllegalArgumentException("User with UID " + uid + " not found in school " + schoolId);
        }

        // Extract hodId from the user document
        String hrId = userSnapshot.getString("hrId");
        if (hrId == null) {
            throw new IllegalArgumentException("No hodId found in user document with UID " + uid);
        }

        // Extract relevant fields from the user document
        Map<String, Object> userData = userSnapshot.getData();
        if (userData != null) {
            // Add user data to hodDetails (only if not already set)
            if (!hrDetails.containsKey("email") && userData.containsKey("email")) {
                hrDetails.put("email", userData.get("email"));
            }
            if (!hrDetails.containsKey("name") && userData.containsKey("name")) {
                hrDetails.put("name", userData.get("name"));
            }
            // Add more fields as needed based on your users collection structure
        }

        // Add extracted hodId and other reference fields to hod details
        hrDetails.put("hrId", hrId);
        hrDetails.put("schoolId", schoolId);

        DocumentReference hodDocument = hodCollection.document(hrId);
        ApiFuture<WriteResult> insertingDataInDocument = hodDocument.set(hrDetails);
    }

}
