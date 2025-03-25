package com.synectiks.school.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.HashMap;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
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
    private final EmailService emailService;
    
    public StudentsDetails(EmailService emailService) {
        this.firestore = FirestoreClient.getFirestore();
        this.emailService = emailService;
    }
    
  //Pushing Student Details
    public String addingStudent(StudentDetails studentDetails, String schoolId) {
        CollectionReference studentCollection = firestore.collection("Student_Details");
        
        
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        // Generate a unique ID for the student document
        String id = UUID.randomUUID().toString();
        studentDetails.setId(id);
        studentDetails.setSchoolId(schoolId);

        // Generate a random password for the student
        String password = generateRandomPassword();
        studentDetails.setPassword(password);

        // Validate email
        String email = studentDetails.getEmail();
        if (email == null || !emailPattern.matcher(email).matches()) {
            return "Error: Invalid email format";
        }

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

        // Send credentials email
        emailService.sendCredentialsEmail(email, password);

        return id;
    }

    private String generateRandomPassword() {
        // Generate a random password (you can customize the length and complexity)
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Map<String, Object> studentDetailsToMap(StudentDetails studentDetails) {
        Map<String, Object> studentData = Maps.newHashMap();
        studentData.put("id", studentDetails.getId());
        studentData.put("schoolId", studentDetails.getSchoolId());
        studentData.put("aadhaarNumber", studentDetails.getAadhaarNumber());
        studentData.put("address", studentDetails.getAddress());
        studentData.put("admissionName", studentDetails.getAdmissionName());
        studentData.put("age", studentDetails.getAge());
        studentData.put("studentClass", studentDetails.getStudentClass());
        studentData.put("dob", studentDetails.getDob());
        studentData.put("email", studentDetails.getEmail());
        studentData.put("fatherName", studentDetails.getFatherName());
        studentData.put("fatherOccupation", studentDetails.getFatherOccupation());
        studentData.put("gender", studentDetails.getGender());
        studentData.put("motherName", studentDetails.getMotherName());
        studentData.put("motherOccupation", studentDetails.getMotherOccupation());
        studentData.put("phoneNumber", studentDetails.getPhoneNumber());
        studentData.put("rollNumber", studentDetails.getRollNumber());
        studentData.put("routeName", studentDetails.getRouteName());
        studentData.put("studentName", studentDetails.getStudentName());
        studentData.put("password", studentDetails.getPassword()); // Include the password

        return studentData;
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
