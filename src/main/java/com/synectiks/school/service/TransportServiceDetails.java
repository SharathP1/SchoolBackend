package com.synectiks.school.service;

import java.awt.event.MouseAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class TransportServiceDetails {

	private static final String COLLECTION_NAME = "Transport_details";
    private final Firestore firestore;
// 
    public TransportServiceDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
	public void addingTransportDetails(List<Map<String, Object>> bustransport)  {
		// TODO Auto-generated method stub
		CollectionReference TransportCollection = firestore.collection(COLLECTION_NAME);
		
		for (Map<String, Object> Busdetails: bustransport) {
			String id = UUID.randomUUID().toString();
			Busdetails.put("id", id);
			
			DocumentReference TransportDocument = TransportCollection.document(id);
			ApiFuture<WriteResult> Inserting_data_in_Document = TransportDocument.set(Busdetails);
			
		}
		
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
	
	//Pushing Fee Details
	public void addingFee(Map<String, Object> FeeDetails, String sid) {
		// TODO Auto-generated method stub
        CollectionReference FeeCollection = firestore.collection("Fee_Details");
		
		String id = UUID.randomUUID().toString();
		FeeDetails.put("id", id);
		FeeDetails.put("sid", sid);
		FeeDetails.put("Payment_On", FieldValue.serverTimestamp()); // Add timestamp
		
		DocumentReference FeeDocument = FeeCollection.document(id);
		ApiFuture<WriteResult> Inserting_data_in_Document = FeeDocument.set(FeeDetails);
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

	public List<Map<String, Object>> getfeedetails() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		CollectionReference feeDetailsTable = firestore.collection("Fee_Details");        
		// Get all documents in the collection        
		ApiFuture<QuerySnapshot> querySnapshot = feeDetailsTable.get();         
		// Process the query snapshot to get document details 
		List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
		List<Map<String, Object>> t = new ArrayList<>();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH); // Format: Day/Month/Year
		
		for (QueryDocumentSnapshot details : documents) {
	        Map<String, Object> data = details.getData();
	        Object timestampObj = data.get("Payment_On");

	        if (timestampObj instanceof Timestamp) {
	            Timestamp timestamp = (Timestamp) timestampObj;
	            Date date = timestamp.toDate();
	            data.put("Payment_On", dateFormat.format(date)); // Convert and format date
	        }

	        t.add(data);
	    }

		return t;
	}
}

