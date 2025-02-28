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
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.school.entity.StudentFeeDetails;

@Service
public class FeeDetails {

    private final Firestore firestore;

    public FeeDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }

    // Pushing Fee Details
    public void addingFee(Map<String, Object> feeDetails, String sid) {
        CollectionReference feeCollection = firestore.collection("Fee_Details");

        String id = UUID.randomUUID().toString();
        feeDetails.put("id", id);
        feeDetails.put("sid", sid);
//        feeDetails.put("Payment_On", FieldValue.serverTimestamp()); // Add timestamp

        DocumentReference feeDocument = feeCollection.document(id);
        ApiFuture<WriteResult> insertingDataInDocument = feeDocument.set(feeDetails);
    }

    // Retrieve all fee details
    public List<StudentFeeDetails> getFeeDetails(String sid, String schoolId) throws InterruptedException, ExecutionException {
    	  CollectionReference feeDetailsTable = firestore.collection("Fee_Details");

          Query query = feeDetailsTable
                  .whereEqualTo("schoolId", schoolId);

          ApiFuture<QuerySnapshot> querySnapshot = query.get();
          List<StudentFeeDetails> feeList = new ArrayList<>();

          for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
              feeList.add(document.toObject(StudentFeeDetails.class)); 
          }
  System.out.println(feeList);
          return feeList;
      }
    
    public List<StudentFeeDetails> getFeeDetails1(String clas, String schoolId) throws InterruptedException, ExecutionException {
        CollectionReference feeDetailsTable = firestore.collection("Fee_Details");

        Query query = feeDetailsTable
                .whereEqualTo("studentClass", clas)
                .whereEqualTo("schoolId", schoolId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<StudentFeeDetails> feeList = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            feeList.add(document.toObject(StudentFeeDetails.class)); 
        }
System.out.println(feeList);
        return feeList;
    }


    // Retrieve fee details based on parameters
//    public List<Map<String, Object>> getFeeDetails(Long aid) throws InterruptedException, ExecutionException {
//        CollectionReference feeDetailsTable = firestore.collection("Fee_Details");
//        Query query = feeDetailsTable;
//
//        if (classname != null) {
//            query = query.whereEqualTo("classname", classname);
//        }
//        if (section != null) {
//            query = query.whereEqualTo("section", section);
//        }
//        if (aid != null) {
//            query = query.whereEqualTo("aid", aid);
//        }
//        if (name != null) {
//            query = query.whereEqualTo("name", name);
//        }
//
//        ApiFuture<QuerySnapshot> querySnapshot = query.get();
//        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
//        List<Map<String, Object>> feeDetailsList = new ArrayList<>();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH); // Format: Day/Month/Year
//
//        for (QueryDocumentSnapshot details : documents) {
//            Map<String, Object> data = details.getData();
//            Object timestampObj = data.get("Payment_On");
//
//            if (timestampObj instanceof Timestamp) {
//                Timestamp timestamp = (Timestamp) timestampObj;
//                Date date = timestamp.toDate();
//                data.put("Payment_On", dateFormat.format(date)); // Convert and format date
//            }
//
//            feeDetailsList.add(data);
//        }
//
//        return feeDetailsList;
//    }
}
