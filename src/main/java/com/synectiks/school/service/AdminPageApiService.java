package com.synectiks.school.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreBundle;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.school.entity.StudentFeeDetails;

@Service
public class AdminPageApiService {
    private final Firestore db = FirestoreClient.getFirestore();

    public String addFeeDetails(StudentFeeDetails transactionDetails, String schoolId) {
        try {
            CollectionReference transactionsCollection = db.collection("Fee_Details");
            transactionDetails.setSchoolId(schoolId);
            ApiFuture<DocumentReference> future = transactionsCollection.add(transactionDetails);
            DocumentReference document = future.get();
            return "Transaction added successfully with ID: " + document.getId();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return "Error adding transaction: " + e.getMessage();
        }
    }

    public List<FeeDetails> getFeeDetailsForCurrentMonth(String schoolId, String sid) throws InterruptedException, ExecutionException {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        ZonedDateTime startOfMonth = currentDate.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusNanos(1);

        Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(startOfMonth.toEpochSecond(), startOfMonth.getNano());
        Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(endOfMonth.toEpochSecond(), endOfMonth.getNano());

        QuerySnapshot querySnapshot = db.collection("Fee_Details")
                .whereEqualTo("schoolId", schoolId)
                .whereEqualTo("sid", sid)
                .whereGreaterThanOrEqualTo("dueDate", startTimestamp)
                .whereLessThanOrEqualTo("dueDate", endTimestamp)
                .get()
                .get();

        return querySnapshot.getDocuments().stream()
                .map(document -> document.toObject(FeeDetails.class))
                .collect(Collectors.toList());
    }
    
    
    public double getTotalPaymentsReceived(String schoolId) {
        double totalAmountPaid = 0;
        try {
            ApiFuture<QuerySnapshot> future = db.collection("Fee_Details").whereEqualTo("schoolId", schoolId).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                if (data != null && data.containsKey("Fee_Details")) {
                    List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) data.get("Fee_Details");
                    for (Map<String, Object> fee : feeDetails) {
                        if (fee.containsKey("amtPaid")) {
                            totalAmountPaid += ((Number) fee.get("amtPaid")).doubleValue();
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return totalAmountPaid;
    }

    public List<Map<String, Object>> getAllPaymentsWithPaidDate(String schoolId) {
        List<Map<String, Object>> paymentList = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("Fee_Details").whereEqualTo("schoolId", schoolId).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                if (data != null && data.containsKey("Fee_Details")) {
                    List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) data.get("Fee_Details");
                    for (Map<String, Object> fee : feeDetails) {
                        if (fee.containsKey("amtPaid") && fee.containsKey("paidDate")) {
                            double amountPaid = ((Number) fee.get("amtPaid")).doubleValue();
                            String paidDate = (String) fee.get("paidDate");
                            if (paidDate != null && !paidDate.isEmpty()) {
                                Map<String, Object> paymentRecord = new HashMap<>();
                                paymentRecord.put("amtPaid", amountPaid);
                                paymentRecord.put("paidDate", paidDate);
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
        return paymentList;
    }

    public List<Map<String, Object>> getFeeDetailsForCurrentWeek(String schoolId) {
        List<Map<String, Object>> feeDetailsList = new ArrayList<>();

        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Determine the start (Monday) and end (Sunday) of the current week
            LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(6);

            Query query = db.collection("Fee_Details").whereEqualTo("schoolId", schoolId);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Map<String, Object> docData = document.getData();

                // Extract the "Fee_Details" array
                List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) docData.get("Fee_Details");
                List<Map<String, Object>> filteredFees = new ArrayList<>();

                for (Map<String, Object> fee : feeDetails) {
                    String paidDateStr = (String) fee.get("paidDate");

                    // Ensure paidDate is not empty before parsing
                    if (paidDateStr != null && !paidDateStr.isEmpty()) {
                        LocalDate paidDate = LocalDate.parse(paidDateStr, formatter);

                        // Check if paidDate falls within the current week
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

    public double getTotalPaymentsReceivedToday(String schoolId) {
        double totalAmountPaidToday = 0;

        try {
            String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            ApiFuture<QuerySnapshot> future = db.collection("Fee_Details").whereEqualTo("schoolId", schoolId).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                if (data != null && data.containsKey("Fee_Details")) {
                    List<Map<String, Object>> feeDetails = (List<Map<String, Object>>) data.get("Fee_Details");

                    for (Map<String, Object> fee : feeDetails) {
                        // Check if paidDate is today
                        if (fee.containsKey("paidDate") && todayDate.equals(fee.get("paidDate"))) {
                            if (fee.containsKey("amtPaid")) {
                                totalAmountPaidToday += ((Number) fee.get("amtPaid")).doubleValue();
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return totalAmountPaidToday;
    }
}
