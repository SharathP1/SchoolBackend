package com.synectiks.school.service;

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
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FeeDetails {
	
	private final Firestore firestore;
	// 
	    public FeeDetails() {
	        this.firestore = FirestoreClient.getFirestore();
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
