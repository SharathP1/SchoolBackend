package com.synectiks.school.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class AttendanceDetails {
	
private final Firestore firestore;
    
    public AttendanceDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
    public void markAttendance(Map<String, Object> attendanceDetail, String sid) {
		// TODO Auto-generated method stub
	    CollectionReference attendanceCollection = firestore.collection("Attendance");

	    String id = UUID.randomUUID().toString();
	    attendanceDetail.put("id", id);
	    attendanceDetail.put("sid", sid);
	    attendanceDetail.put("timestamp", FieldValue.serverTimestamp()); // Add Timestamp

	    DocumentReference attendanceDocument = attendanceCollection.document(id);
	    attendanceDocument.set(attendanceDetail);
	}

    
    public List<Map<String, Object>> getAttendance(String sid) throws InterruptedException, ExecutionException {
	    CollectionReference attendanceCollection = firestore.collection("Attendance");
	    Query query = attendanceCollection.whereEqualTo("sid", sid);
	    
	    ApiFuture<QuerySnapshot> querySnapshot = query.get();
	    List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
	    List<Map<String, Object>> attendanceRecords = new ArrayList<>();

	    for (QueryDocumentSnapshot document : documents) {
	        Map<String, Object> data = document.getData();
	        
	        // Convert timestamp to readable format
	        Timestamp timestamp = (Timestamp) data.get("timestamp");
	        Date date = timestamp.toDate();
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        data.put("formatted_date", sdf.format(date));

	        attendanceRecords.add(data);
	    }

	    return attendanceRecords;
	}

    public void student_attendance_per_period(Map<String, Object> student_attendance_per_period) {
        CollectionReference studentattendanceperperiodCollection = firestore.collection("Student Attendance Per Period");

        String id = UUID.randomUUID().toString();
        student_attendance_per_period.put("id", id);
        student_attendance_per_period.put("timestamp", FieldValue.serverTimestamp()); // Add Timestamp

        DocumentReference attendanceDocument = studentattendanceperperiodCollection.document(id);
        attendanceDocument.set(student_attendance_per_period);
    }
    
    public List<Map<String, Object>> getAttendanceByClassSectionNamePeriod(String classNumber, String section, String name, String period) throws InterruptedException, ExecutionException {
        CollectionReference attendanceCollection = firestore.collection("Student Attendance Per Period");
        Query query;

        if (classNumber != null && section != null && name != null && period != null) {
            query = attendanceCollection.whereEqualTo("Class", classNumber)
                                        .whereEqualTo("Section", section)
                                        .whereEqualTo("name", name)
                                        .whereEqualTo("Period", period);
        } else if (classNumber != null && section != null && name != null) {
            query = attendanceCollection.whereEqualTo("Class", classNumber)
                                        .whereEqualTo("Section", section)
                                        .whereEqualTo("name", name);
        } else if (classNumber != null && section != null && period != null) {
            query = attendanceCollection.whereEqualTo("Class", classNumber)
                                        .whereEqualTo("Section", section)
                                        .whereEqualTo("Period", period);
        } else if (classNumber != null && name != null && period != null) {
            query = attendanceCollection.whereEqualTo("Class", classNumber)
                                        .whereEqualTo("name", name)
                                        .whereEqualTo("Period", period);
        } else if (section != null && name != null && period != null) {
            query = attendanceCollection.whereEqualTo("Section", section)
                                        .whereEqualTo("name", name)
                                        .whereEqualTo("Period", period);
        } else if (classNumber != null && section != null) {
            query = attendanceCollection.whereEqualTo("class", classNumber)
                                        .whereEqualTo("section", section);
        } else if (classNumber != null && name != null) {
            query = attendanceCollection.whereEqualTo("Class", classNumber)
                                        .whereEqualTo("name", name);
        } else if (classNumber != null && period != null) {
            query = attendanceCollection.whereEqualTo("Class", classNumber)
                                        .whereEqualTo("Period", period);
        } else if (section != null && name != null) {
            query = attendanceCollection.whereEqualTo("Section", section)
                                        .whereEqualTo("name", name);
        } else if (section != null && period != null) {
            query = attendanceCollection.whereEqualTo("Section", section)
                                        .whereEqualTo("Period", period);
        } else if (name != null && period != null) {
            query = attendanceCollection.whereEqualTo("name", name)
                                        .whereEqualTo("Period", period);
        } else if (classNumber != null) {
            query = attendanceCollection.whereEqualTo("Class", classNumber);
        } else if (section != null) {
            query = attendanceCollection.whereEqualTo("Section", section);
        } else if (name != null) {
            query = attendanceCollection.whereEqualTo("name", name);
        } else if (period != null) {
            query = attendanceCollection.whereEqualTo("Period", period);
        } else {
            // If none of the parameters are provided, return an empty list or handle accordingly
            return new ArrayList<>();
        }

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Map<String, Object>> attendanceList = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            attendanceList.add(document.getData());
        }
        return attendanceList;
    }


}
