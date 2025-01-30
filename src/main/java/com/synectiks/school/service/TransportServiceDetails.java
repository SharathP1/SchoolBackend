package com.synectiks.school.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class TransportServiceDetails {

//	private static final String COLLECTION_NAME = "Transport_details";
    private final Firestore firestore;
// 
    public TransportServiceDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
	public void addingTransportDetails(List<Map<String, Object>> bustransport)  {
		// TODO Auto-generated method stub
		CollectionReference TransportCollection = firestore.collection("Transport_details");
		
		for (Map<String, Object> Busdetails: bustransport) {
			String id = UUID.randomUUID().toString();
			Busdetails.put("id", id);
			
			DocumentReference TransportDocument = TransportCollection.document(id);
			ApiFuture<WriteResult> Inserting_data_in_Document = TransportDocument.set(Busdetails);
			
		}
		
	}
}

