package com.synectiks.school.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
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

	public String addingStudent(Map<String, Object> studentDetails) {
		// TODO Auto-generated method stub
		CollectionReference StudentCollection = firestore.collection("Student_Details");
		
		String id = UUID.randomUUID().toString();
		studentDetails.put("id", id);
		
		DocumentReference StudentDocument = StudentCollection.document(id);
		ApiFuture<WriteResult> Inserting_data_in_Document = StudentDocument.set(studentDetails);
		return id;
	}

	public void addingTeacher(Map<String, Object> teacherDetails) {
		// TODO Auto-generated method stub
		CollectionReference TeacherCollection = firestore.collection("Teacher_Details");
		
		String id = UUID.randomUUID().toString();
		teacherDetails.put("id", id);
		
		DocumentReference TeacherDocument = TeacherCollection.document(id);
		ApiFuture<WriteResult> Inserting_data_in_Document = TeacherDocument.set(teacherDetails);
	}
}

