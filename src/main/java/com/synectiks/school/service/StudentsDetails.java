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
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class StudentsDetails {
	
    private final Firestore firestore;
    
    public StudentsDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
  //Pushing Student Details
  	public String addingStudent(Map<String, Object> studentDetails) {
  		// TODO Auto-generated method stub
  		CollectionReference StudentCollection = firestore.collection("Student_Details");
  		
  		String id = UUID.randomUUID().toString();
  		studentDetails.put("id", id);
  		
  		DocumentReference StudentDocument = StudentCollection.document(id);
  		ApiFuture<WriteResult> Inserting_data_in_Document = StudentDocument.set(studentDetails);
  		return id;
  	}
  	
  //Getting Complete Students Details
  	public List<Map<String, Object>> getstudentdetails() throws InterruptedException, ExecutionException {
  		// TODO Auto-generated method stub
  		
  		CollectionReference studentDetailsTable = firestore.collection("Student_Details");        
  		// Get all documents in the collection        
  		ApiFuture<QuerySnapshot> querySnapshot = studentDetailsTable.get();         
  		// Process the query snapshot to get document details 
  		List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
  		List<Map<String, Object>> t = new ArrayList<>();

  		for (QueryDocumentSnapshot details : documents) {
  		    // Add the document data (as a map) to the list
  		    t.add(details.getData());
  		}

  		return t;
  	}

}
