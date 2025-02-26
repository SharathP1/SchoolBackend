package com.synectiks.school.service;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class TeacherDetails {

    private final Firestore firestore;

    public TeacherDetails() {
        this.firestore = FirestoreClient.getFirestore();
    }
    
    

    // Adding a Teacher's Details with class name
    public void addTeacher(Map<String, Object> teacherDetails, String className) {
        CollectionReference teacherCollection = firestore.collection("Teacher_Details");

        String id = UUID.randomUUID().toString();
        teacherDetails.put("id", id);
        teacherDetails.put("className", className);

        DocumentReference teacherDocument = teacherCollection.document(id);
        ApiFuture<WriteResult> insertingDataInDocument = teacherDocument.set(teacherDetails);
    }

    // Adding Periods for a Teacher based on Employee Name and Class
    public void addPeriods(String employeeName, String className, Map<String, Object> periods) throws InterruptedException, ExecutionException {
        CollectionReference periodsCollection = firestore.collection("TeacherPeriods");

        String id = UUID.randomUUID().toString();
        periods.put("id", id);
        periods.put("employeeName", employeeName);
        periods.put("className", className);

        DocumentReference periodsDocument = periodsCollection.document(id);
        ApiFuture<WriteResult> insertingDataInDocument = periodsDocument.set(periods);
    }
    
    // Method to add a teacher's timetable
    public void addTimetableForTeacher(String employeeId, String className, Map<String, Object> timetable) throws InterruptedException, ExecutionException {
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");

        String id = UUID.randomUUID().toString();
        timetable.put("id", id);
        timetable.put("employeeId", employeeId);
        timetable.put("className", className);

        DocumentReference timetableDocument = timetableCollection.document(id);
        ApiFuture<WriteResult> insertingDataInDocument = timetableDocument.set(timetable);
    }

    public void addTimetableForClass(String className, Map<String, Object> timetable) throws InterruptedException, ExecutionException {
        className = className.trim(); // Trim any leading or trailing spaces
        CollectionReference timetableCollection = firestore.collection("ClassTimetables");

        // Use className as the document ID
        DocumentReference timetableDocument = timetableCollection.document(className);
        ApiFuture<WriteResult> insertingDataInDocument = timetableDocument.set(timetable);
    }
    
    public void addClubsForClass(String className, Map<String, Object> clubsData) throws InterruptedException, ExecutionException {
        className = className.trim();
        DocumentReference clubsDocument = firestore.collection("ClassClubs").document(className);
        ApiFuture<WriteResult> result = clubsDocument.set(clubsData);
    }



    // Getting complete Teacher Details irrespective of a specific teacher
    public List<Map<String, Object>> getAllTeacherDetails() throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Teacher Details by Teacher Name
    public List<Map<String, Object>> getTeacherDetailsByName(String teacherName) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("name", teacherName).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Teacher Details by Class Name
    public List<Map<String, Object>> getTeacherDetailsByClass(String className) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("className", className).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Teacher Details by Department
    public List<Map<String, Object>> getTeacherDetailsByDepartment(String department) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("department", department).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Teacher Details by HOD Name
    public List<Map<String, Object>> getTeacherDetailsByHODName(String hodName) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable.whereEqualTo("hodName", hodName).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> teacherDetailsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            teacherDetailsList.add(details.getData());
        }

        return teacherDetailsList;
    }

    // Getting Periods for a Teacher by Employee Name and Class
    public List<Map<String, Object>> getPeriodsByEmployeeNameAndClass(String employeeName, String className) throws InterruptedException, ExecutionException {
        CollectionReference periodsCollection = firestore.collection("TeacherPeriods");
        ApiFuture<QuerySnapshot> querySnapshot = periodsCollection.whereEqualTo("employeeName", employeeName).whereEqualTo("className", className).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Map<String, Object>> periodsList = new ArrayList<>();

        for (QueryDocumentSnapshot details : documents) {
            periodsList.add(details.getData());
        }

        return periodsList;
    }

    // Getting Teacher Details by Employee ID and Name
    public Map<String, Object> getTeacherDetailsByEmployeeId(String employeeId) throws InterruptedException, ExecutionException {
        CollectionReference teacherDetailsTable = firestore.collection("Teacher_Details");

        // Firestore query: Filtering both employeeId and name in a single request
        ApiFuture<QuerySnapshot> querySnapshot = teacherDetailsTable
            .whereEqualTo("id", employeeId)
//            .whereEqualTo("name", name)
            .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Teacher not found");
        }

        return documents.get(0).getData();
    }

 // Method to get the teacher's timetable by employee ID
    public Map<String, Object> getTeacherTimeTable(String employeeId) throws InterruptedException, ExecutionException {
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");

        // Firestore query: Filtering by employeeId
        ApiFuture<QuerySnapshot> querySnapshot = timetableCollection
            .whereEqualTo("employeeId", employeeId)
            .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Time table not found for employee ID: " + employeeId);
        }

        return documents.get(0).getData();
    }

    public Map<String, Object> getTimetableForClass(String className) throws InterruptedException, ExecutionException {
        className = className.trim(); // Trim any leading or trailing spaces
        CollectionReference timetableCollection = firestore.collection("ClassTimetables");

        // Use className as the document ID to retrieve the document
        DocumentReference timetableDocument = timetableCollection.document(className);
        ApiFuture<DocumentSnapshot> future = timetableDocument.get();

        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.getData();
        } else {
            throw new RuntimeException("Class timetable not found for className: " + className);
        }
    }





    public Map<String, Object> getTeacherDetailsByEmployeeId1(String employeeId, String name) {
        // Simulate fetching teacher details from a database or an external service
        Map<String, Object> teacherDetails = new HashMap<>();

        // Example data based on the provided image
        teacherDetails.put("attendanceThisMonth", 26);
        teacherDetails.put("totalLeaves", 2);
        teacherDetails.put("totalAvailableLeaves", 12);
        teacherDetails.put("totalAttendance", 28); // Assuming total attendance is the total working days in the month

        return teacherDetails;
    }

    // Method to get attendance and leaves by employee ID
    public Map<String, Object> getTeacherAttendanceLeaves(String employeeId) throws InterruptedException, ExecutionException {
        Map<String, Object> teacherDetails = getTeacherDetailsByEmployeeId(employeeId);

        Map<String, Object> attendanceLeaves = new HashMap<>();
        attendanceLeaves.put("attendanceThisMonth", teacherDetails.get("attendanceThisMonth"));
        attendanceLeaves.put("totalLeaves", teacherDetails.get("totalLeaves"));
        attendanceLeaves.put("totalAvailableLeaves", teacherDetails.get("totalAvailableLeaves"));
        attendanceLeaves.put("totalAttendance", teacherDetails.get("totalAttendance"));

        return attendanceLeaves;
    }

    // Getting Time Table by Employee ID
//    public Map<String, Object> getTeacherTimeTable(String employeeId) throws InterruptedException, ExecutionException {
//        List<Map<String, Object>> periods = getPeriodsByEmployeeNameAndClass(employeeId, null);
//        if (periods.isEmpty()) {
//            throw new RuntimeException("Time table not found");
//        }
//
//        Map<String, Object> timeTable = new HashMap<>();
//        timeTable.put("timeTable", periods);
//
//        return timeTable;
//    }

//    public void addTimetableForClass(String employeeId, String className, Map<String, Object> timetable) throws InterruptedException, ExecutionException {
//        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
//
//        String id = UUID.randomUUID().toString();
//        timetable.put("id", id);
//        timetable.put("employeeId", employeeId);
//        timetable.put("className", className);
//
//        DocumentReference timetableDocument = timetableCollection.document(id);
//        ApiFuture<WriteResult> insertingDataInDocument = timetableDocument.set(timetable);
//    }

//    public List<Map<String, Object>> getTimetableForClass(String employeeId, String className) throws InterruptedException, ExecutionException {
//        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
//        ApiFuture<QuerySnapshot> querySnapshot = timetableCollection.whereEqualTo("employeeId", employeeId).whereEqualTo("className", className).get();
//        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
//        List<Map<String, Object>> timetableList = new ArrayList<>();
//
//        for (QueryDocumentSnapshot details : documents) {
//            timetableList.add(details.getData());
//        }
//
//        return timetableList;
//    }
    
    public Map<String, Object> getClubsForClass(String className) throws InterruptedException, ExecutionException {
        className = className.trim();
        DocumentReference clubsDocument = firestore.collection("ClassClubs").document(className);
        ApiFuture<DocumentSnapshot> future = clubsDocument.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.getData();
        } else {
            throw new RuntimeException("No clubs found for class: " + className);
        }
    }


    public Map<String, Object> getCombinedTimetableForTeacher(String employeeId) throws InterruptedException, ExecutionException {
        CollectionReference timetableCollection = firestore.collection("TeacherTimetables");
        ApiFuture<QuerySnapshot> querySnapshot = timetableCollection.whereEqualTo("employeeId", employeeId).get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        Map<String, Object> combinedTimetable = new HashMap<>();

        // Initialize the structure for the combined timetable
        Map<String, Map<String, String>> timetable = new HashMap<>();
        for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"}) {
            timetable.put(day, new HashMap<String, String>() {{
                put("1st period", "");
                put("2nd period", "");
                put("3rd period", "");
                put("4th period", "");
                put("5th period", "");
                put("6th period", "");
                put("7th period", "");
                put("8th period", "");
            }});
        }

        // Populate the timetable
        for (QueryDocumentSnapshot details : documents) {
            Map<String, Object> timetableData = details.getData();
            String className = (String) timetableData.get("className");
            Map<String, Object> periods = (Map<String, Object>) timetableData.get("periods");

            for (String day : periods.keySet()) {
                List<String> classes = (List<String>) periods.get(day);
                for (int i = 0; i < classes.size(); i++) {
                    String division = switch (i) {
                        case 0 -> "1st period";
                        case 1 -> "2nd period";
                        case 2 -> "3rd period";
                        case 3 -> "4th period";
                        case 4 -> "5th period";
                        case 5 -> "6th period";
                        case 6 -> "7th period";
                        case 7 -> "8th period";
                        default -> "";
                    };
                    timetable.get(day).put(division, classes.get(i));
                }
            }
        }

        combinedTimetable.put("timetable", timetable);
        return combinedTimetable;
    }

    // New methods for LessonPlan

 // Adding a Lesson Plan
    public void addLessonPlan(String id, Map<String, Object> lessonPlan) throws InterruptedException, ExecutionException {
        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");
        CollectionReference teacherDetailsCollection = firestore.collection("Teacher_Details");

        // Validate if the teacher with the provided id exists
        QuerySnapshot querySnapshot = teacherDetailsCollection.whereEqualTo("id", id).get().get();
        if (querySnapshot.isEmpty()) {
            throw new IllegalArgumentException("Teacher with the provided id does not exist.");
        }

        String lessonPlanId = UUID.randomUUID().toString();
        lessonPlan.put("id", lessonPlanId);
        lessonPlan.put("createdAt", new Date());
        lessonPlan.put("updatedAt", new Date());
        lessonPlan.put("teacherId", id); // Use the id as a reference

        DocumentReference lessonPlanDocument = lessonPlanCollection.document(lessonPlanId);
        ApiFuture<WriteResult> insertingDataInDocument = lessonPlanDocument.set(lessonPlan);
        insertingDataInDocument.get(); // Wait for the write to complete
    }

    public List<Map<String, Object>> getLessonPlan(String id) throws InterruptedException, ExecutionException {
        CollectionReference lessonPlanCollection = firestore.collection("LessonPlans");

        ApiFuture<QuerySnapshot> querySnapshot = lessonPlanCollection
            .whereEqualTo("teacherId", id)
            .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Lesson plans not found for employee ID: " + id);
        }

        List<Map<String, Object>> lessonPlans = new ArrayList<>();
        SimpleDateFormat istDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        istDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone

        for (QueryDocumentSnapshot document : documents) {
            Map<String, Object> lessonPlan = document.getData();

            // Convert Timestamp to Date and format
            Timestamp createdAtTimestamp = (Timestamp) lessonPlan.get("createdAt");
            Timestamp updatedAtTimestamp = (Timestamp) lessonPlan.get("updatedAt");

            lessonPlan.put("createdAt", istDateFormat.format(new Date(createdAtTimestamp.getSeconds() * 1000)));
            lessonPlan.put("updatedAt", istDateFormat.format(new Date(updatedAtTimestamp.getSeconds() * 1000)));

            lessonPlans.add(lessonPlan);
        }

        return lessonPlans;
    }
    
    public void deleteLessonPlan(String lessonPlanId) throws InterruptedException, ExecutionException {
        DocumentReference lessonPlanDocument = firestore.collection("LessonPlans").document(lessonPlanId);
        ApiFuture<WriteResult> deleteResult = lessonPlanDocument.delete();
        deleteResult.get(); // Wait for the delete to complete
    }
    
    public void addStudentPortfoliosForClass(String className, Map<String, Object> portfoliosData) throws InterruptedException, ExecutionException {
        className = className.trim();
        DocumentReference portfoliosDocument = firestore.collection("StudentPortfolios").document(className);
        ApiFuture<WriteResult> result = portfoliosDocument.set(portfoliosData);
    }

    
    public Map<String, Object> getStudentPortfoliosForClass(String className) throws InterruptedException, ExecutionException {
        className = className.trim();
        DocumentReference portfoliosDocument = firestore.collection("StudentPortfolios").document(className);
        ApiFuture<DocumentSnapshot> future = portfoliosDocument.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.getData();
        } else {
            throw new RuntimeException("No portfolios found for class: " + className);
        }
    }




    public void updateLessonPlan(String id, Map<String, Object> lessonPlan) throws InterruptedException, ExecutionException {
        // Query the collection to find the document with the matching 'id' field
        ApiFuture<QuerySnapshot> querySnapshot = firestore.collection("LessonPlans").whereEqualTo("id", id).get();
        QuerySnapshot queryResult = querySnapshot.get();

        if (!queryResult.isEmpty()) {
            // Get the first document that matches the query
            DocumentSnapshot documentSnapshot = queryResult.getDocuments().get(0);
            DocumentReference lessonPlanDocument = documentSnapshot.getReference();

            // Update the document with the new data
            ApiFuture<WriteResult> updatingDataInDocument = lessonPlanDocument.set(lessonPlan);
            // Optionally, you can wait for the update to complete and handle the result
            System.out.println("Update time: " + updatingDataInDocument.get().getUpdateTime());
        } else {
            System.out.println("No document found with the given id.");
        }

    }
    
    
}
