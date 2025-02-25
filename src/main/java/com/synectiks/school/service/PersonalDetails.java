package com.synectiks.school.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class PersonalDetails {
	
//	private static final String COLLECTION_NAME = "Transport_details";
    private final Firestore firestore;
    
    public PersonalDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
	//Pushing a Student's Personal Details with student ID 
		public void addingParent(Map<String, Object> PersonalDetails, String sid) {
			// TODO Auto-generated method stub
	        CollectionReference PersonalCollection = firestore.collection("Personal_Details");
			
			String id = UUID.randomUUID().toString();
			PersonalDetails.put("id", id);
			PersonalDetails.put("sid", sid);
			
			DocumentReference TeacherDocument = PersonalCollection.document(id);
			ApiFuture<WriteResult> Inserting_data_in_Document = TeacherDocument.set(PersonalDetails);
		}
		
		
		//Getting complete Personal Details irrespective a specific student
		public List<Map<String, Object>> getpersonaldetails() throws InterruptedException, ExecutionException {
			// TODO Auto-generated method stub
			CollectionReference personalDetailsTable = firestore.collection("Personal_Details");        
			// Get all documents in the collection        
			ApiFuture<QuerySnapshot> querySnapshot = personalDetailsTable.get();         
			// Process the query snapshot to get document details 
			List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
			List<Map<String, Object>> t = new ArrayList<>();

			for (QueryDocumentSnapshot details : documents) {
			    // Add the document data (as a map) to the list
			    t.add(details.getData());
			}

			return t;
		}
		
		public List<Map<String, Object>> getpersonaldetailsById(String sid) throws InterruptedException, ExecutionException {
		    CollectionReference personalDetailsTable = firestore.collection("Personal_Details");
 
		    // Query the collection to find documents where the 'sid' field matches the given sid
		    Query query = personalDetailsTable.whereEqualTo("sid", sid);
		    ApiFuture<QuerySnapshot> querySnapshot = query.get();
 
		    List<Map<String, Object>> t = new ArrayList<>();
 
		    for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
		        // Add the document data (as a map) to the list
		        t.add(document.getData());
		    }
 
		    return t;
		}
}
