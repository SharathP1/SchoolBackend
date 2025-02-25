package com.synectiks.school.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class AdminPageApiService {
	  private final Firestore db = FirestoreClient.getFirestore();

	    public String addFeeDetails(Map<String, Object> transactionDetails) {
	        try {
	            CollectionReference transactionsCollection = db.collection("Fee_Details");
	            transactionDetails.put("date", LocalDate.now().format(DateTimeFormatter.ISO_DATE)); // Storing date as yyyy-MM-dd
	            ApiFuture<DocumentReference> future = transactionsCollection.add(transactionDetails);
	            DocumentReference document = future.get();
	            return "Transaction added successfully with ID: " + document.getId();
	        } catch (InterruptedException | ExecutionException e) {
	            Thread.currentThread().interrupt();
	            return "Error adding transaction: " + e.getMessage();
	        }
	    }

//	    public List<Map<String, Object>> getFeeDetailsForCurrentMonth() {
//	        List<Map<String, Object>> feeDetailsList = new ArrayList<>();
//	        try {
//	            LocalDate now = LocalDate.now();
//	            String firstDay = now.withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE);
//	            String lastDay = now.withDayOfMonth(now.lengthOfMonth()).format(DateTimeFormatter.ISO_DATE);
//	            System.out.println(firstDay);
//	            System.out.println(lastDay);
//	            Query query = db.collection("Fee_Transaction_Details")
//	                .whereGreaterThanOrEqualTo("date", firstDay)
//	                .whereLessThanOrEqualTo("date", lastDay);
//	            
//	            ApiFuture<QuerySnapshot> querySnapshot = query.get();
//	            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
//	                feeDetailsList.add(document.getData());
//	            }
//	        } catch (InterruptedException | ExecutionException e) {
//	            Thread.currentThread().interrupt();
//	        }
//	        System.out.println(feeDetailsList);
//	        return feeDetailsList;
//	    }
	    public List<Map<String, Object>> getFeeDetailsForCurrentMonth() {
	        List<Map<String, Object>> feeDetailsList = new ArrayList<>();
	        
	        try {
	            LocalDate now = LocalDate.now();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	            int currentMonth = now.getMonthValue();
	            int currentYear = now.getYear();

	            Query query = db.collection("Fee_Details");
	            ApiFuture<QuerySnapshot> querySnapshot = query.get();

	            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
	                Map<String, Object> docData = document.getData();
	                System.out.println(docData);
	                // Extract the "Fee_Details" array
	                List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) docData.get("Fee_Details");
	                System.out.println(feeDetails);
	                List<Map<String, Object>> filteredFees = new ArrayList<>();

	                for (Map<String, Object> fee : feeDetails) {
	                    String paidDateStr = (String) fee.get("paid_date");

	                    // Ensure the date is not empty before parsing
	                    if (paidDateStr != null && !paidDateStr.isEmpty()) {
	                        LocalDate paidDate = LocalDate.parse(paidDateStr, formatter);
	                        
	                        // Check if the paid date falls in the current month
	                        if (paidDate.getMonthValue() == currentMonth && paidDate.getYear() == currentYear) {
	                            filteredFees.add(fee);
	                        }
	                    }
	                }

	                // If any fees match, add them to the result list
	                if (!filteredFees.isEmpty()) {
	                    Map<String, Object> filteredData = new HashMap<>(docData);
	                    filteredData.put("Fee_Details", filteredFees);
	                    feeDetailsList.add(filteredData);
	                }
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            Thread.currentThread().interrupt();
	            e.printStackTrace();
	        }

	        return feeDetailsList;
	    }

	    public List<Map<String, Object>> getFeeDetailsForCurrentWeek() {
	        List<Map<String, Object>> feeDetailsList = new ArrayList<>();
	        
	        try {
	            LocalDate now = LocalDate.now();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	            // Determine the start (Monday) and end (Sunday) of the current week
	            LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	            LocalDate endOfWeek = startOfWeek.plusDays(6);

	            Query query = db.collection("Fee_Details");
	            ApiFuture<QuerySnapshot> querySnapshot = query.get();

	            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
	                Map<String, Object> docData = document.getData();
	                
	                // Extract the "Fee_Details" array
	                List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) docData.get("Fee_Details");
	                List<Map<String, Object>> filteredFees = new ArrayList<>();

	                for (Map<String, Object> fee : feeDetails) {
	                    String paidDateStr = (String) fee.get("paid_date");

	                    // Ensure paid_date is not empty before parsing
	                    if (paidDateStr != null && !paidDateStr.isEmpty()) {
	                        LocalDate paidDate = LocalDate.parse(paidDateStr, formatter);
	                        
	                        // Check if paid_date falls within the current week
	                        if (!paidDate.isBefore(startOfWeek) && !paidDate.isAfter(endOfWeek)) {
	                            filteredFees.add(fee);
	                        }
	                    }
	                }

	                // If any fees match, add them to the result list
	                if (!filteredFees.isEmpty()) {
	                    Map<String, Object> filteredData = new HashMap<>(docData);
	                    filteredData.put("Fee_Details", filteredFees);
	                    feeDetailsList.add(filteredData);
	                }
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            Thread.currentThread().interrupt();
	            e.printStackTrace();
	        }

	        return feeDetailsList;
	    }

	    public double getTotalPaymentsReceived() {
	        double totalAmountPaid = 0;

	        try {
	            ApiFuture<QuerySnapshot> future = db.collection("Fee_Details").get();
	            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

	            for (QueryDocumentSnapshot document : documents) {
	                Map<String, Object> data = document.getData();

	                if (data != null && data.containsKey("Fee_Details")) {
	                    List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) data.get("Fee_Details");

	                    for (Map<String, Object> fee : feeDetails) {
	                        if (fee.containsKey("amt_paid")) {
	                            totalAmountPaid += ((Number) fee.get("amt_paid")).doubleValue();
	                        }
	                    }
	                }
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            Thread.currentThread().interrupt();
	            e.printStackTrace();
	        }

	        System.out.println("Total Amount Paid: " + totalAmountPaid);
	        return totalAmountPaid;
	    }
	    public double getTotalPaymentsReceivedToday() {
	        double totalAmountPaidToday = 0;
	        
	        try {
	            // Get the current date in the format "dd/MM/yyyy"
	            String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
System.out.println(todayDate);
	            // Fetch all documents from Fee_Transaction_Details
	            ApiFuture<QuerySnapshot> future = db.collection("Fee_Details").get();
	            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

	            for (QueryDocumentSnapshot document : documents) {
	                Map<String, Object> data = document.getData();

	                if (data != null && data.containsKey("Fee_Details")) {
	                    List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) data.get("Fee_Details");

	                    for (Map<String, Object> fee : feeDetails) {
	                        // Check if paid_date is today
	                        if (fee.containsKey("paid_date") && todayDate.equals(fee.get("paid_date"))) {
	                            if (fee.containsKey("amt_paid")) {
	                                totalAmountPaidToday += ((Number) fee.get("amt_paid")).doubleValue();
	                            }
	                        }
	                    }
	                }
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            Thread.currentThread().interrupt();
	            e.printStackTrace();
	        }

	        System.out.println("Total Amount Paid Today: " + totalAmountPaidToday);
	        return totalAmountPaidToday;
	    }
	    public List<Map<String, Object>> getAllPaymentsWithPaidDate() {
	        List<Map<String, Object>> paymentList = new ArrayList<>();

	        try {
	            // Fetch all documents from Fee_Transaction_Details
	            ApiFuture<QuerySnapshot> future = db.collection("Fee_Details").get();
	            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

	            for (QueryDocumentSnapshot document : documents) {
	                Map<String, Object> data = document.getData();

	                if (data != null && data.containsKey("Fee_Details")) {
	                    List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) data.get("Fee_Details");

	                    for (Map<String, Object> fee : feeDetails) {
	                        // Check if amt_paid is greater than 0 (i.e., payment was made)
	                        if (fee.containsKey("amt_paid") && fee.containsKey("paid_date")) {
	                            double amountPaid = ((Number) fee.get("amt_paid")).doubleValue();
	                            String paidDate = (String) fee.get("paid_date");

	                            // Add to list only if paid_date is not empty
	                            if (paidDate != null && !paidDate.isEmpty()) {
	                                Map<String, Object> paymentRecord = new HashMap<>();
	                                paymentRecord.put("amt_paid", amountPaid);
	                                paymentRecord.put("paid_date", paidDate);

	                                paymentList.add(paymentRecord);
	                            }
	                        }
	                    }
	                }
	            }
	        } catch (InterruptedException | ExecutionException e) {
	            Thread.currentThread().interrupt();
	            e.printStackTrace();
	        }

	        System.out.println(paymentList);
	        return paymentList;
	    }
}
