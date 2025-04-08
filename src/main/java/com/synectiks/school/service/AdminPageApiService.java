package com.synectiks.school.service;

import java.time.DayOfWeek;
import java.util.logging.Logger;
import com.google.cloud.firestore.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.synectiks.school.entity.DeleteMarksRequest;
import com.synectiks.school.entity.StudentFeeDetails;
import com.synectiks.school.entity.StudentMarks;
import com.synectiks.school.entity.Term;

@Service
public class AdminPageApiService {
    private final Firestore db = FirestoreClient.getFirestore();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final Logger logger = Logger.getLogger(AdminPageApiService.class.getName());


    public String addFeeDetails(StudentFeeDetails feeDetail, String schoolId, String id) {
        try {
            // Log received data
            Gson gson = new Gson();
            String feeDetailJson = gson.toJson(feeDetail);
            logger.info("Received feeDetail: " + feeDetailJson);
            logger.info("Received schoolId: " + schoolId);
            logger.info("Received id: " + id);

            // Check for null values
            if (feeDetail == null) {
                return "Error: feeDetail is null.";
            }
            if (schoolId == null || schoolId.isEmpty()) {
                return "Error: schoolId is null or empty.";
            }
            if (id == null || id.isEmpty()) {
                return "Error: id is null or empty.";
            }

            // Fetch student document from Student_Details
            Query query = db.collection("Student_Details")
                            .whereEqualTo("schoolId", schoolId)
                            .whereEqualTo("id", id);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            if (documents.isEmpty()) {
                return "Error: Student not found.";
            }

            QueryDocumentSnapshot studentDocument = documents.get(0);
            String sid = studentDocument.getId(); // document ID from Student_Details

            Map<String, Object> studentData = studentDocument.getData();
            String studentName = (String) studentData.get("studentName");
            String rollNumber = (String) studentData.get("rollNumber");
            String studentClass = (String) studentData.get("studentClass");
            String studentId = (String) studentData.get("id");

            // Convert amtPaid to string if needed
            if (feeDetail.getAmtPaid() != null) {
                feeDetail.setAmtPaid(String.valueOf(feeDetail.getAmtPaid()));
            }

            // Reference to Fee_Details collection
            DocumentReference feeDocRef = db.collection("Fee_Details").document(sid);
            ApiFuture<DocumentSnapshot> feeDocSnapshot = feeDocRef.get();
            DocumentSnapshot document = feeDocSnapshot.get();

            if (document.exists()) {
                // Append new feeDetail into existing array
                ApiFuture<WriteResult> updateFuture = feeDocRef.update(
                    "feeDetails", FieldValue.arrayUnion(feeDetail)
                );
                updateFuture.get();
            } else {
                // Create new document if not exists
                Map<String, Object> newDoc = new HashMap<>();
                newDoc.put("schoolId", schoolId);
                newDoc.put("studentName", studentName);
                newDoc.put("rollNumber", rollNumber);
                newDoc.put("studentClass", studentClass);
                newDoc.put("studentId", studentId);
                newDoc.put("feeDetails", Arrays.asList(feeDetail));

                ApiFuture<WriteResult> createFuture = feeDocRef.set(newDoc);
                createFuture.get();
            }

            return "Fee detail added successfully for student ID: " + sid;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            logger.severe("Error adding fee detail: " + e.getMessage());
            return "Error adding fee detail: " + e.getMessage();
        }
    }


    public void updateFeeDetailsBySid(String sid, String schoolId, List<Map<String, Object>> newFeeDetails) {
        try {
            CollectionReference transactionsCollection = db.collection("Fee_Details");
            Query query = transactionsCollection.whereEqualTo("sid", sid).whereEqualTo("schoolId", schoolId);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            QuerySnapshot queryResult = querySnapshot.get();

            if (!queryResult.isEmpty()) {
                QueryDocumentSnapshot document = queryResult.getDocuments().get(0);
                DocumentReference documentReference = document.getReference();

                Map<String, Object> existingData = document.getData();
                List<Map<String, Object>> existingFeeDetails = (List<Map<String, Object>>) existingData.get("feedetails");

                // Initialize existingFeeDetails if it is null
                if (existingFeeDetails == null) {
                    existingFeeDetails = new ArrayList<>();
                }

                // Add new fee details to existing fee details
                for (Map<String, Object> newFeeDetail : newFeeDetails) {
                    if (!"Paid".equals(newFeeDetail.get("status"))) {
                        newFeeDetail.remove("paidDate");
                    }
                    existingFeeDetails.add(newFeeDetail);
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("feedetails", existingFeeDetails);

                ApiFuture<WriteResult> writeResult = documentReference.update(updates);
                writeResult.get();
                System.out.println("Fee details updated successfully.");
            } else {
                System.out.println("Document with sid " + sid + " and schoolId " + schoolId + " does not exist.");
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            System.out.println("Error updating fee details: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getAllFeeDetails(String schoolId, String studentId) {
        try {
            Query query = db.collection("Fee_Details")
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("studentId", studentId);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            if (documents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No fee details found.");
            }

            // Get the data from the first document that matches the query
            Map<String, Object> data = documents.get(0).getData();

            // Return the entire data map as the response
            return ResponseEntity.ok(data);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // 2. Get fee details for the current month
    public ResponseEntity<?> getMonthlyFeeDetails(String schoolId, String studentId) {
        try {
            List<Map<String, Object>> feeDetails = getFeeDetailsList(schoolId, studentId);
            if (feeDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No fee details found.");
            }

            YearMonth currentMonth = YearMonth.now();
            List<Map<String, Object>> filtered = feeDetails.stream()
                .filter(item -> {
                    String dueDateStr = (String) item.get("dueDate");
                    if (dueDateStr == null) return false;
                    LocalDate dueDate = LocalDate.parse(dueDateStr, formatter);
                    return YearMonth.from(dueDate).equals(currentMonth);
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(filtered);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // 3. Get fee details for the current week
    public ResponseEntity<?> getWeeklyFeeDetails(String schoolId, String studentId) {
        try {
            List<Map<String, Object>> feeDetails = getFeeDetailsList(schoolId, studentId);
            if (feeDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No fee details found.");
            }

            LocalDate now = LocalDate.now();
            LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

            List<Map<String, Object>> filtered = feeDetails.stream()
                .filter(item -> {
                    String dueDateStr = (String) item.get("dueDate");
                    if (dueDateStr == null) return false;
                    LocalDate dueDate = LocalDate.parse(dueDateStr, formatter);
                    return !dueDate.isBefore(startOfWeek) && !dueDate.isAfter(endOfWeek);
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(filtered);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Utility: fetch list of fee details
    private List<Map<String, Object>> getFeeDetailsList(String schoolId, String studentId)
            throws InterruptedException, ExecutionException {
        Query query = db.collection("Fee_Details")
                .whereEqualTo("schoolId", schoolId)
                .whereEqualTo("studentId", studentId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) return null;

        Map<String, Object> data = documents.get(0).getData();
        return (List<Map<String, Object>>) data.get("feeDetails");
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

    public List<FeeDetails> getFeeDetailsForNextMonth(String schoolId, String sid) throws InterruptedException, ExecutionException {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextMonthDate = currentDate.plusMonths(1);
        int nextMonth = nextMonthDate.getMonthValue();
        int nextYear = nextMonthDate.getYear();

        ZonedDateTime startOfNextMonth = nextMonthDate.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfNextMonth = nextMonthDate.withDayOfMonth(nextMonthDate.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusNanos(1);

        Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(startOfNextMonth.toEpochSecond(), startOfNextMonth.getNano());
        Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(endOfNextMonth.toEpochSecond(), endOfNextMonth.getNano());

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

    public List<FeeDetails> getFeeDetailsForPreviousMonth(String schoolId, String sid) throws InterruptedException, ExecutionException {
        LocalDate currentDate = LocalDate.now();
        LocalDate previousMonthDate = currentDate.minusMonths(1);
        int previousMonth = previousMonthDate.getMonthValue();
        int previousYear = previousMonthDate.getYear();

        ZonedDateTime startOfPreviousMonth = previousMonthDate.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfPreviousMonth = previousMonthDate.withDayOfMonth(previousMonthDate.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusNanos(1);

        Timestamp startTimestamp = Timestamp.ofTimeSecondsAndNanos(startOfPreviousMonth.toEpochSecond(), startOfPreviousMonth.getNano());
        Timestamp endTimestamp = Timestamp.ofTimeSecondsAndNanos(endOfPreviousMonth.toEpochSecond(), endOfPreviousMonth.getNano());

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

    // Term Management Methods
    public String addTerm(String schoolId, Map<String, Object> termData)
            throws InterruptedException, ExecutionException {

        termData.put("schoolId", schoolId);  // Add school ID into term data
        String termId = UUID.randomUUID().toString();  // Generate a unique term ID
        termData.put("termId", termId);  // Add the generated term ID to the term data

        // Generate test IDs for each assessment dynamically
        List<Map<String, Object>> assessments = (List<Map<String, Object>>) termData.get("assessments");
        if (assessments != null) {
            for (Map<String, Object> assessment : assessments) {
                String testId = UUID.randomUUID().toString();
                assessment.put("testId", testId);  // Add the generated test ID to the assessment
            }
        }

        // Use the termId as the document ID
        DocumentReference docRef = db.collection("curriculum").document(termId);
        ApiFuture<WriteResult> future = docRef.set(termData);
        future.get();  // Ensure the document is added

        return termId;  // Return the generated term ID
    }


    public List<Term> getTerms(String schoolId) throws InterruptedException, ExecutionException {
        Query query = db.collection("curriculum").whereEqualTo("schoolId", schoolId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<Term> terms = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Term term = document.toObject(Term.class);
            term.setTermId(document.getId());  // Set the termId from the document ID
            terms.add(term);
        }
        return terms;
    }



    public void deleteTerm(String schoolId, String termId) throws InterruptedException, ExecutionException {
        Query query = db.collection("curriculum")
                        .whereEqualTo("schoolId", schoolId)
                        .whereEqualTo("termId", termId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            document.getReference().delete(); // This will delete the entire term document
        }
    }

    
    public void deleteAssessment(String schoolId, String termId, String assessmentId) throws InterruptedException, ExecutionException {
        Query query = db.collection("curriculum")
                        .whereEqualTo("schoolId", schoolId)
                        .whereEqualTo("termId", termId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            DocumentReference docRef = document.getReference();

            // Get existing assessments
            List<Map<String, Object>> assessments = (List<Map<String, Object>>) document.get("assessments");
            if (assessments != null) {
                List<Map<String, Object>> updatedAssessments = new ArrayList<>();

                for (Map<String, Object> assessment : assessments) {
                    if (!assessmentId.equals(assessment.get("testId"))) {
                        updatedAssessments.add(assessment);
                    }
                }

                // Update the document with filtered list
                docRef.update("assessments", updatedAssessments);
            }
        }
    }
    
    public void enterMarks(String schoolId, String termId, String testId, String teacherId, String className, List<StudentMarks> studentMarksList)
            throws InterruptedException, ExecutionException {

        Logger logger = Logger.getLogger(this.getClass().getName());

        // Verify teacher ID and retrieve teacher name
        DocumentReference teacherDocRef = db.collection("Teacher_Details").document(teacherId);
        ApiFuture<DocumentSnapshot> teacherDocFuture = teacherDocRef.get();
        DocumentSnapshot teacherDoc = teacherDocFuture.get();

        if (!teacherDoc.exists()) {
            throw new IllegalArgumentException("Teacher ID does not exist: " + teacherId);
        }
        String teacherName = teacherDoc.getString("name");

        for (StudentMarks studentMarks : studentMarksList) {
            String studentId = studentMarks.getStudentId();

            // Retrieve student details and student name
            DocumentReference studentDocRef = db.collection("Student_Details").document(studentId);
            ApiFuture<DocumentSnapshot> studentDocFuture = studentDocRef.get();
            DocumentSnapshot studentDoc = studentDocFuture.get();

            if (studentDoc.exists()) {
                String retrievedClassName = studentDoc.getString("studentClass");
                String studentName = studentDoc.getString("studentName");
                logger.info("Retrieved className for studentId " + studentId + ": " + retrievedClassName);

                if (retrievedClassName == null) {
                    throw new IllegalArgumentException("ClassName is null for student ID: " + studentId);
                }

                // Verify that the provided className matches the student's className
                if (!retrievedClassName.equals(className)) {
                    throw new IllegalArgumentException("ClassName mismatch for student ID: " + studentId);
                }

                studentMarks.setClassName(className);

                // Save marks in the curriculum collection
                DocumentReference curriculumDocRef = db.collection("curriculum").document(termId);
                ApiFuture<DocumentSnapshot> curriculumDocFuture = curriculumDocRef.get();
                DocumentSnapshot curriculumDoc = curriculumDocFuture.get();

                if (curriculumDoc.exists()) {
                    Map<String, Object> curriculumData = curriculumDoc.getData();
                    List<Map<String, Object>> assessments = (List<Map<String, Object>>) curriculumData.get("assessments");

                    for (Map<String, Object> assessment : assessments) {
                        if (assessment.get("testId").equals(testId)) {
                            List<Map<String, Object>> marksArray = (List<Map<String, Object>>) assessment.get("marks");
                            if (marksArray == null) {
                                marksArray = new ArrayList<>();
                            }

                            // Data for the curriculum marks array
                            Map<String, Object> marksData = new HashMap<>();
                            marksData.put("studentId", studentId);
                            marksData.put("studentName", studentName);
                            marksData.put("className", className);
                            marksData.put("subject", studentMarks.getSubject());
                            marksData.put("marks", studentMarks.getMarks());
                            marksData.put("teacherId", teacherId);
                            marksData.put("teacherName", teacherName);
                            marksData.put("timestamp", new Date().getTime()); // Client-side timestamp

                            marksArray.add(marksData);
                            assessment.put("marks", marksArray);

                            curriculumDocRef.set(curriculumData, SetOptions.merge());
                            logger.info("Marks entered successfully for studentId " + studentId + " in class " + className);

                            // Save or append to the marks subcollection with studentId as document ID
                            DocumentReference marksDocRef = db.collection("curriculum").document(termId)
                                                            .collection("tests").document(testId)
                                                            .collection("marks").document(studentId);

                            // Fetch existing document (if any)
                            ApiFuture<DocumentSnapshot> marksDocFuture = marksDocRef.get();
                            DocumentSnapshot marksDoc = marksDocFuture.get();

                            Map<String, Object> marksSubcollectionData = new HashMap<>();
                            if (marksDoc.exists()) {
                                // Document exists, append new subject and teacher data
                                Map<String, Object> existingData = marksDoc.getData();
                                List<Map<String, Object>> subjects = (List<Map<String, Object>>) existingData.get("subjects");
                                if (subjects == null) {
                                    subjects = new ArrayList<>();
                                }

                                Map<String, Object> subjectData = new HashMap<>();
                                subjectData.put("subject", studentMarks.getSubject());
                                subjectData.put("marks", studentMarks.getMarks());
                                subjectData.put("teacherId", teacherId);
                                subjectData.put("teacherName", teacherName);
                                subjectData.put("timestamp", new Date().getTime()); // Client-side timestamp

                                subjects.add(subjectData);
                                marksSubcollectionData.put("subjects", subjects);
                                // Preserve existing fields
                                marksSubcollectionData.put("studentId", studentId);
                                marksSubcollectionData.put("studentName", studentName);
                                marksSubcollectionData.put("className", className);
                            } else {
                                // New document, initialize with first subject
                                List<Map<String, Object>> subjects = new ArrayList<>();
                                Map<String, Object> subjectData = new HashMap<>();
                                subjectData.put("subject", studentMarks.getSubject());
                                subjectData.put("marks", studentMarks.getMarks());
                                subjectData.put("teacherId", teacherId);
                                subjectData.put("teacherName", teacherName);
                                subjectData.put("timestamp", new Date().getTime()); // Client-side timestamp

                                subjects.add(subjectData);
                                marksSubcollectionData.put("subjects", subjects);
                                marksSubcollectionData.put("studentId", studentId);
                                marksSubcollectionData.put("studentName", studentName);
                                marksSubcollectionData.put("className", className);
                            }

                            // Add a top-level lastUpdated field with server timestamp
                            marksSubcollectionData.put("lastUpdated", FieldValue.serverTimestamp());

                            marksDocRef.set(marksSubcollectionData, SetOptions.merge());
                            logger.info("Marks stored successfully for studentId " + studentId + " in the marks collection");
                            break;
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Term ID does not exist: " + termId);
                }
            } else {
                throw new IllegalArgumentException("Student ID does not exist: " + studentId);
            }
        }
    }
    
    
    public List<StudentMarks> viewMarksByClass(String schoolId, String termId, String testId, String className)
            throws InterruptedException, ExecutionException {

        CollectionReference marksCollection = db.collection("curriculum").document(termId)
                                                .collection("tests").document(testId)
                                                .collection("marks");
        Query query = marksCollection.whereEqualTo("className", className);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<StudentMarks> marksList = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Map<String, Object> data = document.getData();
            String studentId = (String) data.get("studentId");
            String studentName = (String) data.get("studentName");
            String retrievedClassName = (String) data.get("className");
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) data.get("subjects");

            if (subjects != null) {
                for (Map<String, Object> subjectData : subjects) {
                    StudentMarks studentMarks = new StudentMarks();
                    studentMarks.setStudentId(studentId);
                    studentMarks.setStudentName(studentName);
                    studentMarks.setClassName(retrievedClassName);
                    studentMarks.setSubject((String) subjectData.get("subject"));
                    studentMarks.setMarks(((Number) subjectData.get("marks")).intValue()); // Assuming marks is a number
                    studentMarks.setTeacherId((String) subjectData.get("teacherId"));
                    studentMarks.setTeacherName((String) subjectData.get("teacherName"));
                    studentMarks.setTimestamp(((Number) subjectData.get("timestamp")).longValue());

                    marksList.add(studentMarks);
                }
            }
        }

        return marksList;
    }

    
    public List<StudentMarks> viewMarksByStudent(String schoolId, String termId, String testId, String studentId)
            throws InterruptedException, ExecutionException {

        DocumentReference marksDocRef = db.collection("curriculum").document(termId)
                                          .collection("tests").document(testId)
                                          .collection("marks").document(studentId);
        ApiFuture<DocumentSnapshot> marksDocFuture = marksDocRef.get();
        DocumentSnapshot marksDoc = marksDocFuture.get();

        List<StudentMarks> marksList = new ArrayList<>();

        if (marksDoc.exists()) {
            Map<String, Object> data = marksDoc.getData();
            String studentName = (String) data.get("studentName");
            String className = (String) data.get("className");
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) data.get("subjects");

            if (subjects != null) {
                for (Map<String, Object> subjectData : subjects) {
                    StudentMarks studentMarks = new StudentMarks();
                    studentMarks.setStudentId(studentId);
                    studentMarks.setStudentName(studentName);
                    studentMarks.setClassName(className);
                    studentMarks.setSubject((String) subjectData.get("subject"));
                    studentMarks.setMarks(((Number) subjectData.get("marks")).intValue());
                    studentMarks.setTeacherId((String) subjectData.get("teacherId"));
                    studentMarks.setTeacherName((String) subjectData.get("teacherName"));
                    studentMarks.setTimestamp(((Number) subjectData.get("timestamp")).longValue());

                    marksList.add(studentMarks);
                }
            }
        }

        return marksList;
    }

    
    public List<StudentMarks> viewMarksBySubject(String schoolId, String termId, String testId, String subject)
            throws InterruptedException, ExecutionException {

        CollectionReference marksCollection = db.collection("curriculum").document(termId)
                                                .collection("tests").document(testId)
                                                .collection("marks");

        ApiFuture<QuerySnapshot> querySnapshot = marksCollection.get(); // Get all documents
        List<StudentMarks> marksList = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Map<String, Object> data = document.getData();
            String studentId = (String) data.get("studentId");
            String studentName = (String) data.get("studentName");
            String className = (String) data.get("className");
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) data.get("subjects");

            if (subjects != null) {
                for (Map<String, Object> subjectData : subjects) {
                    if (subject.equals(subjectData.get("subject"))) { // Filter by subject
                        StudentMarks studentMarks = new StudentMarks();
                        studentMarks.setStudentId(studentId);
                        studentMarks.setStudentName(studentName);
                        studentMarks.setClassName(className);
                        studentMarks.setSubject((String) subjectData.get("subject"));
                        studentMarks.setMarks(((Number) subjectData.get("marks")).intValue());
                        studentMarks.setTeacherId((String) subjectData.get("teacherId"));
                        studentMarks.setTeacherName((String) subjectData.get("teacherName"));
                        studentMarks.setTimestamp(((Number) subjectData.get("timestamp")).longValue());

                        marksList.add(studentMarks);
                    }
                }
            }
        }

        return marksList;
    }
    
    public void updateMarks(String schoolId, String termId, String testId, String teacherId, String className, List<StudentMarks> updatedMarksList)
            throws InterruptedException, ExecutionException {
        if (updatedMarksList == null || updatedMarksList.isEmpty()) {
            throw new IllegalArgumentException("Updated marks list cannot be null or empty");
        }

        // Fetch teacher name from Teacher_Details
        DocumentReference teacherDocRef = db.collection("Teacher_Details").document(teacherId);
        ApiFuture<DocumentSnapshot> teacherDocFuture = teacherDocRef.get();
        DocumentSnapshot teacherDoc = teacherDocFuture.get();
        if (!teacherDoc.exists()) {
            throw new IllegalArgumentException("Teacher ID does not exist: " + teacherId);
        }
        String teacherName = teacherDoc.getString("name");

        for (StudentMarks updatedMarks : updatedMarksList) {
            // Validate required fields
            if (updatedMarks.getStudentId() == null || updatedMarks.getSubject() == null) {
                throw new IllegalArgumentException("studentId and subject are required for each marks entry");
            }

            DocumentReference marksDocRef = db.collection("curriculum").document(termId)
                                              .collection("tests").document(testId)
                                              .collection("marks").document(updatedMarks.getStudentId());
            ApiFuture<DocumentSnapshot> marksDocFuture = marksDocRef.get();
            DocumentSnapshot marksDoc = marksDocFuture.get();

            // Fetch student name from Student_Details
            DocumentReference studentDocRef = db.collection("Student_Details").document(updatedMarks.getStudentId());
            ApiFuture<DocumentSnapshot> studentDocFuture = studentDocRef.get();
            DocumentSnapshot studentDoc = studentDocFuture.get();
            String studentName = studentDoc.exists() ? studentDoc.getString("studentName") : "Unknown";

            Map<String, Object> data = marksDoc.exists() ? marksDoc.getData() : new HashMap<>();
            List<Map<String, Object>> subjects = (List<Map<String, Object>>) data.get("subjects");
            if (subjects == null) {
                subjects = new ArrayList<>();
            }

            boolean updated = false;
            for (Map<String, Object> subjectData : subjects) {
                if (updatedMarks.getSubject().equals(subjectData.get("subject"))) {
                    subjectData.put("marks", updatedMarks.getMarks());
                    subjectData.put("teacherId", teacherId);
                    subjectData.put("teacherName", teacherName);
                    subjectData.put("timestamp", new Date().getTime());
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                Map<String, Object> newSubject = new HashMap<>();
                newSubject.put("subject", updatedMarks.getSubject());
                newSubject.put("marks", updatedMarks.getMarks());
                newSubject.put("teacherId", teacherId);
                newSubject.put("teacherName", teacherName);
                newSubject.put("timestamp", new Date().getTime());
                subjects.add(newSubject);
            }

            data.put("studentId", updatedMarks.getStudentId());
            data.put("studentName", studentName);
            data.put("className", className);
            data.put("subjects", subjects);
            data.put("lastUpdated", FieldValue.serverTimestamp());

            marksDocRef.set(data, SetOptions.merge());
            logger.info("Marks updated for studentId: " + updatedMarks.getStudentId() + ", subject: " + updatedMarks.getSubject());
        }
    }
    
    public void deleteMarks(String schoolId, String termId, String testId, String teacherId, String className, List<DeleteMarksRequest> deleteRequests)
            throws InterruptedException, ExecutionException {
        if (deleteRequests == null || deleteRequests.isEmpty()) {
            throw new IllegalArgumentException("Delete requests list cannot be null or empty");
        }

        for (DeleteMarksRequest request : deleteRequests) {
            if (request.getStudentId() == null || request.getSubject() == null) {
                throw new IllegalArgumentException("studentId and subject are required for each delete request");
            }

            DocumentReference marksDocRef = db.collection("curriculum").document(termId)
                                              .collection("tests").document(testId)
                                              .collection("marks").document(request.getStudentId());
            ApiFuture<DocumentSnapshot> marksDocFuture = marksDocRef.get();
            DocumentSnapshot marksDoc = marksDocFuture.get();

            if (marksDoc.exists()) {
                Map<String, Object> data = marksDoc.getData();
                List<Map<String, Object>> subjects = (List<Map<String, Object>>) data.get("subjects");

                if (subjects != null) {
                    subjects.removeIf(subjectData -> request.getSubject().equals(subjectData.get("subject")));
                    data.put("subjects", subjects);
                    data.put("lastUpdated", FieldValue.serverTimestamp());
                    marksDocRef.set(data, SetOptions.merge());
                    logger.info("Marks deleted for studentId: " + request.getStudentId() + ", subject: " + request.getSubject());
                    if (subjects.isEmpty()) {
                        marksDocRef.delete();
                        logger.info("Deleted empty marks document for studentId: " + request.getStudentId());
                    }
                }
            } else {
                logger.warning("No marks found for studentId: " + request.getStudentId() + ", skipping deletion");
            }
        }
    }
}
