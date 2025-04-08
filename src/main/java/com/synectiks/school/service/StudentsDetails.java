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
    
    public String addingStudent(StudentDetails studentDetails, String schoolId, String uid) {
        // Reference to Firestore collections
        CollectionReference studentCollection = firestore.collection("Student_Details");
        CollectionReference schoolsCollection = firestore.collection("schools");
        
        // Reference to users subcollection under the specific school
        DocumentReference userDoc = schoolsCollection
            .document(schoolId)
            .collection("users")
            .document(uid);
        
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        
        String sid;
        // Fetch user data from Firestore
        try {
            ApiFuture<DocumentSnapshot> userFuture = userDoc.get();
            DocumentSnapshot userSnapshot = userFuture.get();
            
            if (userSnapshot.exists()) {
                // Extract student ID (sid) from the user document
                sid = userSnapshot.getString("sid");
                
                if (sid == null) {
                    return "Error: No student ID (sid) found in user document";
                }
                
                // Use sid as the document ID
                studentDetails.setId(sid);
                studentDetails.setSchoolId(schoolId);

                // Generate a random password for the student
                String password = generateRandomPassword();
                studentDetails.setPassword(password);

                // Merge user data into studentDetails
                Map<String, Object> userData = userSnapshot.getData();
                if (userData != null) {
                    // Add user data to studentDetails (only if not already set)
                    if (studentDetails.getEmail() == null && userData.containsKey("email")) {
                        studentDetails.setEmail((String) userData.get("email"));
                    }
                    if (studentDetails.getStudentName() == null && userData.containsKey("name")) {
                        studentDetails.setStudentName((String) userData.get("name"));
                    }
                    // Add more fields as needed based on your users collection structure
                }
            } else {
                return "Error: User with UID " + uid + " not found in school " + schoolId;
            }
        } catch (InterruptedException | ExecutionException e) {
            return "Error: Failed to fetch user data";
        }

        // Validate email (after potentially getting it from user data)
        String email = studentDetails.getEmail();
        if (email == null || !emailPattern.matcher(email).matches()) {
            return "Error: Invalid email format";
        }

        // Verify if the school exists with the given schoolId
        DocumentReference schoolDoc = schoolsCollection.document(schoolId);
        try {
            ApiFuture<DocumentSnapshot> schoolFuture = schoolDoc.get();
            DocumentSnapshot schoolSnapshot = schoolFuture.get();
            if (!schoolSnapshot.exists()) {
                return "Error: School with ID " + schoolId + " does not exist";
            }
        } catch (InterruptedException | ExecutionException e) {
            return "Error: Failed to verify school existence";
        }

        // Convert StudentDetails to a Map for Firestore
        Map<String, Object> studentData = studentDetailsToMap(studentDetails);

        // Use sid as the document ID
        DocumentReference studentDocument = studentCollection.document(sid);
        ApiFuture<WriteResult> resultFuture = studentDocument.set(studentData);

        try {
            resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }

        // Send credentials email
        emailService.sendCredentialsEmail(email, studentDetails.getPassword());

        return sid;
    }

    	private String generateRandomPassword() {
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
//    	    studentData.put("password", studentDetails.getPassword());
    	    
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

	
	  public Map<String, Object> getStudentById(String schoolId,String id) throws ExecutionException, InterruptedException {
	    	CollectionReference studentCollection = firestore.collection("Student_Details");

	        // Query by schoolId and rollNumber
	        ApiFuture<QuerySnapshot> future = studentCollection
	                .whereEqualTo("schoolId", schoolId)
	                .whereEqualTo("id", id)
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
