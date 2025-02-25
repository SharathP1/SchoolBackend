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
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
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
  	
	public List<Map<String, Object>> getstudentdetailsbyClass(String class1) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		CollectionReference studentDetailsTable = firestore.collection("Student_Details");
 
	    // Query the collection to find documents where the 'id' field matches the given sid
	    Query query = studentDetailsTable.whereEqualTo("Class", class1);
	    ApiFuture<QuerySnapshot> querySnapshot = query.get();
 
	    List<Map<String, Object>> t = new ArrayList<>();
 
	    for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
	        // Add the document data (as a map) to the list
	        t.add(document.getData());
	    }
 
	    return t;
	}
 
	public List<Map<String, Object>> getstudentdetailsbyclassandsection(String class1, String section) throws InterruptedException, ExecutionException {
	    CollectionReference studentDetailsTable = firestore.collection("Student_Details");
 
	    // Query the collection to find documents where the 'Class' field matches the given class1 and 'Section' field matches the given section
	    Query query = studentDetailsTable.whereEqualTo("Class", class1).whereEqualTo("Section", section);
	    ApiFuture<QuerySnapshot> querySnapshot = query.get();
 
	    List<Map<String, Object>> t = new ArrayList<>();
 
	    for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
	        // Add the document data (as a map) to the list
	        t.add(document.getData());
	    }
 
	    return t;
	}
<<<<<<< HEAD
	
	    public Map<String, Object> getStudentById(String id) throws ExecutionException, InterruptedException {
	        DocumentReference studentDocument = firestore.collection("Student_Details").document(id);
	        ApiFuture<DocumentSnapshot> future = studentDocument.get();
	        DocumentSnapshot document = future.get();
	        if (document.exists()) {
	            return document.getData();
	        } else {
	            return null;
	        }
	    }
	    
	    public Map<String, Object> getTransportDetailsByRouteName(String routeName) throws ExecutionException, InterruptedException {
	        CollectionReference transportCollection = firestore.collection("Transport_details");
	        Query query = transportCollection.whereEqualTo("Route_Name", routeName);
	        ApiFuture<QuerySnapshot> querySnapshot = query.get();
	        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
	        if (!documents.isEmpty()) {
	            return documents.get(0).getData();
	        } else {
	            return null;
	        }
	    }
=======
>>>>>>> origin/rishi

}
