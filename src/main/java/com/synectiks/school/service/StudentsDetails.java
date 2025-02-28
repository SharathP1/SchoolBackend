package com.synectiks.school.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;
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

import com.synectiks.school.entity.StudentDetails;

@Service
public class StudentsDetails {
	
    private final Firestore firestore;
    
    public StudentsDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
  //Pushing Student Details
    public String addingStudent(StudentDetails studentDetails, String schoolId) {
        CollectionReference studentCollection = firestore.collection("Student_Details");

        // Generate a unique ID for the student document
        String id = UUID.randomUUID().toString();
        studentDetails.setId(id);
        studentDetails.setSchoolId(schoolId);

        // Convert StudentDetails to a Map for Firestore
        Map<String, Object> studentData = studentDetailsToMap(studentDetails);

        DocumentReference studentDocument = studentCollection.document(id);
        ApiFuture<WriteResult> resultFuture = studentDocument.set(studentData);

        try {
            // Optionally, wait for the write operation to complete
            resultFuture.get();
           
        } catch (InterruptedException | ExecutionException e) {
           
            return null;
        }

        return id;
    }

    private Map<String, Object> studentDetailsToMap(StudentDetails studentDetails) {
        // Convert StudentDetails object to a Map for Firestore
        return new HashMap<String, Object>() {{
            put("aadhaarNumber", studentDetails.getAadhaarNumber());
            put("address", studentDetails.getAddress());
            put("admissionName", studentDetails.getAdmissionName());
            put("age", studentDetails.getAge());
            put("studentClass", studentDetails.getStudentClass());
            put("dob", studentDetails.getDob());
            put("email", studentDetails.getEmail());
            put("fatherName", studentDetails.getFatherName());
            put("fatherOccupation", studentDetails.getFatherOccupation());
            put("gender", studentDetails.getGender());
            put("motherName", studentDetails.getMotherName());
            put("motherOccupation", studentDetails.getMotherOccupation());
            put("phoneNumber", studentDetails.getPhoneNumber());
            put("rollNumber", studentDetails.getRollNumber());
            put("routeName", studentDetails.getRouteName());
          
            put("studentName", studentDetails.getStudentName());
            put("id", studentDetails.getId());
            put("schoolId", studentDetails.getSchoolId());
        }};
    }
  //Getting Complete Students Details
  	public List<StudentDetails> getstudentdetails(String schoolId) throws InterruptedException, ExecutionException {
  	
        
        CollectionReference studentDetailsTable = firestore.collection("Student_Details");

        ApiFuture<QuerySnapshot> querySnapshot = studentDetailsTable.whereEqualTo("schoolId", schoolId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
        List<StudentDetails> studentDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            // Convert Firestore document to StudentDetails object
            StudentDetails student = details.toObject(StudentDetails.class);
            studentDetailsList.add(student);
        }

        return studentDetailsList;
    }
    
  	
  	
	public List<StudentDetails> getstudentdetailsbyClass(String class1,String schoolId) throws InterruptedException, ExecutionException {
		 CollectionReference studentDetailsTable = firestore.collection("Student_Details");

		    Query query = studentDetailsTable
		            .whereEqualTo("studentClass", class1)
		            .whereEqualTo("schoolId", schoolId);

		    ApiFuture<QuerySnapshot> querySnapshot = query.get();
		    List<StudentDetails> studentList = new ArrayList<>();

		    for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
		        studentList.add(document.toObject(StudentDetails.class)); // Convert directly
		    }

		    return studentList;
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

	
	  public Map<String, Object> getStudentById(String schoolId,String rollNumber) throws ExecutionException, InterruptedException {
	    	CollectionReference studentCollection = firestore.collection("Student_Details");

	        // Query by schoolId and rollNumber
	        ApiFuture<QuerySnapshot> future = studentCollection
	                .whereEqualTo("schoolId", schoolId)
	                .whereEqualTo("rollNumber", rollNumber)
	                .limit(1) // Fetch only one result
	                .get();

	        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
	        
	        if (!documents.isEmpty()) {
	            return documents.get(0).getData();
	        } else {
	            return Collections.emptyMap();
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


}
