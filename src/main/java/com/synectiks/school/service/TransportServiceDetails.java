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
import com.synectiks.school.entity.BusRoute;

@Service
public class TransportServiceDetails {

//	private static final String COLLECTION_NAME = "Transport_details";
    private final Firestore firestore;
// 
    public TransportServiceDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
	public void addingTransportDetails(List<BusRoute> bustransport,String schoolId)  {
		 CollectionReference transportCollection = firestore.collection("Transport_details");

	        for (BusRoute busDetails : bustransport) {
	            try {
	            	 String id = UUID.randomUUID().toString();
	                 busDetails.setId(id);
	                 busDetails.setSchoolId(schoolId);
	                DocumentReference transportDocument = transportCollection.document(id);
	                ApiFuture<WriteResult> resultFuture = transportDocument.set(busDetails);

	                // Optionally, wait for the write operation to complete
	                resultFuture.get();
	                
	            } catch (Exception e) {
	               
	            }
	        }
	    }
	
	public List<BusRoute> getTransportDetailsByRouteNameAndSchoolId(String routeName, String schoolId) throws ExecutionException, InterruptedException {
	    List<BusRoute> transportDetailsList = new ArrayList<>();
	    CollectionReference transportCollection = firestore.collection("Transport_details");

	    ApiFuture<QuerySnapshot> querySnapshot = transportCollection
	        .whereEqualTo("routeName", routeName)
	        .whereEqualTo("schoolId", schoolId)
	        .get();

	    QuerySnapshot queryResults = querySnapshot.get();

	    for (QueryDocumentSnapshot document : queryResults) {
	        BusRoute busRoute = document.toObject(BusRoute.class);
	        transportDetailsList.add(busRoute);
	    }

	    return transportDetailsList;
	}

}

