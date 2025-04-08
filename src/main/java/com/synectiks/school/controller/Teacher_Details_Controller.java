package com.synectiks.school.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.DeleteExchange;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.school.service.TeacherAttendanceDetails;
import com.synectiks.school.service.TeacherDetails;

@RestController
@CrossOrigin
public class Teacher_Details_Controller {
	
	

    @Autowired
    private TeacherDetails teacherDetailsService;
	private Firestore firestore;
    
    
    

 // Endpoint to add a new teacher
    @PostMapping("/{schoolId}/{hodId}/{uid}/addTeacher")
    public ResponseEntity<String> addTeacher(@PathVariable String schoolId,
                                             @PathVariable String hodId,
                                             @PathVariable String uid,
                                             @RequestBody Map<String, Object> requestBody) {
        try {
            String className = (String) requestBody.get("className");
            Map<String, Object> teacherDetails = (Map<String, Object>) requestBody.get("teacherDetails");

            // Call the modified addTeacher method with the uid
            String teacherId = teacherDetailsService.addTeacher(teacherDetails, className, schoolId, hodId, uid);
            return ResponseEntity.ok("Teacher details added successfully! Teacher ID: " + teacherId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding teacher: " + e.getMessage());
        }
    }




    // Endpoint to add periods for a teacher based on employee name and class
    @PostMapping("/{schoolId}/addPeriods")
    public String addPeriods(@PathVariable String schoolId, @RequestBody Map<String, Object> requestBody) throws InterruptedException, ExecutionException {
        String employeeName = (String) requestBody.get("employeeName");
        String className = (String) requestBody.get("className");
        Map<String, Object> periods = (Map<String, Object>) requestBody.get("periods");
        teacherDetailsService.addPeriods(employeeName, className, periods, schoolId);
        return "Periods added successfully!";
    }

    
// // Endpoint to add timetable for a teacher's allotted class
//    @PostMapping("/addTimetableForClass")
//    public String addTimetableForClass(@RequestBody Map<String, Object> requestBody) throws InterruptedException, ExecutionException {
//        String employeeId = (String) requestBody.get("employeeId");
//        String className = (String) requestBody.get("className");
//        Map<String, Object> timetable = (Map<String, Object>) requestBody.get("timetable");
//        teacherDetailsService.addTimetableForClass(employeeId, className, timetable);
//        return "Timetable added successfully!";
//    }
    
    @PostMapping("/teacher-attendance/{schoolId}/{teacherId}/{hodId}")
    public ResponseEntity<String> storeTeacherAttendanceDetails(
        @PathVariable String schoolId,
        @PathVariable String teacherId,
        @PathVariable String hodId,
        @RequestBody List<TeacherAttendanceDetails> teacherAttendanceDetailsList) {

        // Set the schoolId, teacherId, and hodId in each teacherAttendanceDetails object
        for (TeacherAttendanceDetails teacherAttendanceDetails : teacherAttendanceDetailsList) {
            teacherAttendanceDetails.setSchoolId(schoolId);
            teacherAttendanceDetails.setTid(teacherId);
            teacherAttendanceDetails.setHodId(hodId);
        }

        String result = teacherDetailsService.storeTeacherAttendanceDetails(teacherAttendanceDetailsList);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/day-wise-teacher-attendance/{schoolId}/{teacherId}/{hodId}")
    public ResponseEntity<String> storeDayWiseTeacherAttendanceDetails(
        @PathVariable String schoolId,
        @PathVariable String teacherId,
        @PathVariable String hodId,
        @RequestBody List<TeacherAttendanceDetails> teacherAttendanceDetailsList) {

        // Set the schoolId, teacherId, and hodId in each teacherAttendanceDetails object
        for (TeacherAttendanceDetails teacherAttendanceDetails : teacherAttendanceDetailsList) {
            teacherAttendanceDetails.setSchoolId(schoolId);
            teacherAttendanceDetails.setTid(teacherId);
            teacherAttendanceDetails.setHodId(hodId);
        }

        String result = teacherDetailsService.storeDayWiseTeacherAttendanceDetails(teacherAttendanceDetailsList);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/teacher-attendance/{schoolId}/{teacherId}/{hodId}")
    public ResponseEntity<Map<String, Object>> getTeacherAttendanceDetails(
        @PathVariable String schoolId,
        @PathVariable String teacherId,
        @PathVariable String hodId) {

        Map<String, Object> attendanceDetails = teacherDetailsService.getTeacherAttendanceDetails(schoolId, teacherId, hodId);
        return ResponseEntity.ok(attendanceDetails);
    }
    
    @GetMapping("/day-wise-teacher-attendance/{schoolId}/{hodId}/{date}")
    public ResponseEntity<List<Map<String, Object>>> getDayWiseTeacherAttendanceDetails(
        @PathVariable String schoolId,
        @PathVariable String hodId,
        @PathVariable String date) {

        List<Map<String, Object>> attendanceDetails = teacherDetailsService.getDayWiseTeacherAttendanceDetails(schoolId, hodId, date);
        return ResponseEntity.ok(attendanceDetails);
    }

    
    @GetMapping("/all-hod-teacher-attendance/{schoolId}/{hodId}")
    public ResponseEntity<List<Map<String, Object>>> getTeacherAttendanceDetails(
        @PathVariable String schoolId,
        @PathVariable String hodId) {

        List<Map<String, Object>> attendanceDetails = teacherDetailsService.getTeacherHODAttendanceDetails(schoolId, hodId);
        return ResponseEntity.ok(attendanceDetails);
    }




    
    @PostMapping("/{schoolId}/addTeacherTimetable")
    public ResponseEntity<String> addTeacherTimetable(@PathVariable String schoolId, @RequestBody Map<String, Object> timetableData) {
        try {
            String employeeId = (String) timetableData.get("employeeId");
            String className = (String) timetableData.get("subject"); // Assuming "subject" is the class name
            Map<String, Object> periods = (Map<String, Object>) timetableData.get("periods");

            teacherDetailsService.addTimetableForTeacher(employeeId, className, periods, schoolId);
            return ResponseEntity.ok("Teacher timetable added successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding teacher timetable");
        }
    }


    
    @PostMapping("/{schoolId}/addClasstimetable")
    public ResponseEntity<String> addClassTimetable(@PathVariable String schoolId, @RequestBody Map<String, Object> timetableData) {
        try {
            String className = (String) timetableData.get("className");
            Map<String, Object> periods = (Map<String, Object>) timetableData.get("timetable");

            teacherDetailsService.addTimetableForClass(className, periods, schoolId);
            return ResponseEntity.ok("Class timetable added successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding class timetable: " + e.getMessage());
        }
    }

    
    @PostMapping("/{schoolId}/{className}/clubs")
    public ResponseEntity<String> addClubsForClass(@PathVariable String schoolId, @PathVariable String className, @RequestBody Map<String, Object> clubsData) {
        try {
            teacherDetailsService.addClubsForClass(className, clubsData, schoolId);
            return ResponseEntity.ok("Clubs added successfully for class: " + className);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding clubs");
        }
    }

    
    @PostMapping("/{schoolId}/{className}/portfolios")
    public ResponseEntity<String> addStudentPortfoliosForClass(@PathVariable String schoolId, @PathVariable String className, @RequestBody Map<String, Object> portfoliosData) {
        try {
            teacherDetailsService.addStudentPortfoliosForClass(schoolId, className, portfoliosData);
            return ResponseEntity.ok("Student portfolios added successfully for class: " + className);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding portfolios");
        }
    }


    @PostMapping("/addLessonPlan/{teacherId}/{schoolId}/{hodId}")
    public ResponseEntity<String> addLessonPlan(
            @PathVariable String teacherId,
            @PathVariable String schoolId,
            @PathVariable String hodId,
            @RequestBody Map<String, Object> lessonPlan) {

        try {
            teacherDetailsService.addLessonPlan(teacherId, schoolId, hodId, lessonPlan);
            return ResponseEntity.ok("Lesson plan added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add lesson plan: " + e.getMessage());
        } catch (ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add lesson plan: " + e.getMessage());
        }
    }
    
    
    @PostMapping("/upload/teacher/{teacherId}/class/{classNumber}/school/{schoolId}")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @PathVariable String teacherId,
            @PathVariable String classNumber,
            @PathVariable String schoolId,
            @RequestBody DocumentUploadRequest request) {
        try {
            Map<String, Object> document = teacherDetailsService.uploadDocument(
                    teacherId,
                    classNumber,
                    request.getFileContent(),
                    request.getFileName(),
                    request.getFileType(),
                    schoolId
            );
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }





    // Endpoint to get all teacher details
    @GetMapping("/getAllTeacherDetails")
    public List<Map<String, Object>> getAllTeacherDetails(@RequestParam String schoolId) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getAllTeacherDetails(schoolId);
    }


    // Endpoint to get teacher details by name
    @GetMapping("/getTeacherDetailsByName")
    public List<Map<String, Object>> getTeacherDetailsByName(@RequestParam String teacherName) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getTeacherDetailsByName(teacherName);
    }

    // Endpoint to get teacher details by class
    @GetMapping("/getTeacherDetailsByClass")
    public List<Map<String, Object>> getTeacherDetailsByClass(@RequestParam String className, @RequestParam String schoolId) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getTeacherDetailsByClass(className, schoolId);
    }


    // Endpoint to get teacher details by department
    @GetMapping("/getTeacherDetailsByDepartment")
    public List<Map<String, Object>> getTeacherDetailsByDepartment(@RequestParam String department) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getTeacherDetailsByDepartment(department);
    }

    // Endpoint to get teacher details by HOD name
    @GetMapping("/getTeacherDetailsByHODName")
    public List<Map<String, Object>> getTeacherDetailsByHODName(@RequestParam String hodName) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getTeacherDetailsByHODName(hodName);
    }

    // Endpoint to get periods for a teacher by employee name and class
    @GetMapping("/getPeriodsByEmployeeNameAndClass")
    public List<Map<String, Object>> getPeriodsByEmployeeNameAndClass(@RequestParam String employeeName, @RequestParam String className, @RequestParam String schoolId) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getPeriodsByEmployeeNameAndClass(employeeName, className, schoolId);
    }


    // Endpoint to get teacher details by employee ID
    @GetMapping("/getTeacherDetailsByEmployeeId")
    public Map<String, Object> getTeacherDetailsByEmployeeId(@RequestParam String id, @RequestParam String schoolId) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getTeacherDetailsByEmployeeId(id, schoolId);
    }

//    @GetMapping("/getTeacherAttendanceLeaves")
//    public Map<String, Object> getTeacherAttendanceLeaves(@RequestParam String employeeId) throws InterruptedException, ExecutionException {
//        return teacherDetailsService.getTeacherAttendanceLeaves(employeeId);
//    }

    @GetMapping("/getteachertimetable/{employeeId}/{schoolId}")
    public ResponseEntity<Map<String, Object>> getTeacherTimetable(@PathVariable String employeeId, @PathVariable String schoolId) {
        try {
            Map<String, Object> timetable = teacherDetailsService.getTeacherTimeTable(employeeId, schoolId);
            return ResponseEntity.ok(timetable);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/getallteachertimetables/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getAllTeacherTimetables(@PathVariable String schoolId) {
        try {
            List<Map<String, Object>> timetables = teacherDetailsService.getAllTeacherTimetables(schoolId);
            return ResponseEntity.ok(timetables);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    
    @GetMapping("/getclasstimetable/{className}/{schoolId}")
    public ResponseEntity<?> getClassTimetable(@PathVariable String className, @PathVariable String schoolId) {
        try {
            Map<String, Object> timetable = teacherDetailsService.getTimetableForClass(className, schoolId);
            return ResponseEntity.ok(timetable);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error retrieving class timetable: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(e.getMessage());
        }
    }


    // Endpoint to get combined timetable for all allotted classes of a teacher
    @GetMapping("/getCombinedTimetableForTeacher")
    public Map<String, Object> getCombinedTimetableForTeacher(@RequestParam String employeeId, @RequestParam String schoolId) throws InterruptedException, ExecutionException {
        return teacherDetailsService.getCombinedTimetableForTeacher(employeeId, schoolId);
    }

    
    @GetMapping("/getLessonPlan/{id}/{schoolId}")
    public List<Map<String, Object>> getLessonPlan(@PathVariable String id, @PathVariable String schoolId) 
            throws InterruptedException, ExecutionException {
        return teacherDetailsService.getLessonPlan(id, schoolId);
    }



    
    @GetMapping("/{className}/clubs")
    public ResponseEntity<Map<String, Object>> getClubsForClass(@PathVariable String className) {
        try {
            // Retrieve the clubs for the specified class
            Map<String, Object> clubs = teacherDetailsService.getClubsForClass(className);
            // Return the clubs data with an OK status
            return ResponseEntity.ok(clubs);
        } catch (InterruptedException | ExecutionException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // Return an internal server error response with a message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", "Error retrieving clubs: " + e.getMessage()));
        } catch (RuntimeException e) {
            // Return a not found response with a message
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{schoolId}/{className}/portfolios")
    public ResponseEntity<?> getStudentPortfoliosForClass(@PathVariable String schoolId, @PathVariable String className) {
        try {
            // Retrieve the student portfolios for the specified class and school ID
            Map<String, Object> portfolios = teacherDetailsService.getStudentPortfoliosForClass(schoolId, className);
            // Return the portfolios data with an OK status
            return ResponseEntity.ok(portfolios);
        } catch (InterruptedException | ExecutionException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // Return an internal server error response with a message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", "Error retrieving portfolios: " + e.getMessage()));
        } catch (RuntimeException e) {
            // Return a not found response with a message
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    
    @GetMapping("/homework/class/{classNumber}/school/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getHomework(
            @PathVariable String classNumber,
            @PathVariable String schoolId) {
        try {
            List<Map<String, Object>> homeworkList = teacherDetailsService.getHomework(classNumber, schoolId);
            return ResponseEntity.ok(homeworkList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonList(Collections.singletonMap("error", e.getMessage())));
        }
    }






    
    //Update Apis
    
 // Endpoint to update a lesson plan
    @PutMapping("/updateLessonPlan/{id}/{schoolId}/{teacherId}/{hodId}")
    public ResponseEntity<Map<String, Object>> updateLessonPlan(
            @PathVariable String hodId,
            @PathVariable String teacherId,
            @PathVariable String id,
            @PathVariable String schoolId,
            @RequestBody Map<String, Object> lessonPlan) throws InterruptedException, ExecutionException, IOException {

        lessonPlan.put("status", "pending");
        lessonPlan.put("hodId", hodId);
        lessonPlan.put("teacherId", teacherId);

        // Call the service method with the required parameters
        teacherDetailsService.updateLessonPlan(id, teacherId, schoolId, hodId, lessonPlan);
        this.firestore = FirestoreClient.getFirestore();

        // Fetch the updated lesson plan to get the updateTime
        DocumentReference lessonPlanDocument = firestore.collection("LessonPlans").document(id);
        ApiFuture<DocumentSnapshot> future = lessonPlanDocument.get();
        DocumentSnapshot document = future.get();

        if (!document.exists()) {
            throw new IllegalArgumentException("Lesson plan with the provided ID does not exist.");
        }

        Map<String, Object> updatedResult = document.getData();

        // Format updateTime in yyyy-MM-dd hh:mm a format
        String formattedTime = null;
        Object updateTime = updatedResult.get("updatedAt");

        if (updateTime instanceof Timestamp) {
            LocalDateTime localDateTime = ((Timestamp) updateTime).toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            formattedTime = localDateTime.format(formatter);
        } else if (updateTime != null) {
            formattedTime = updateTime.toString(); // fallback
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "pending");
        response.put("hodId", hodId);
        response.put("teacherId", teacherId);
        response.put("topicName", updatedResult.get("topicName"));
        response.put("className", updatedResult.get("className"));
        response.put("id", id);
        response.put("updateTime", formattedTime);
        response.put("message", "Lesson plan updated successfully!");

        return ResponseEntity.ok(response);
    }

    
//    Delete Apis
    @DeleteMapping("/deleteLessonPlan/{lessonPlanId}/{schoolId}")
    public ResponseEntity<Map<String, String>> deleteLessonPlan(@PathVariable String lessonPlanId, @PathVariable String schoolId) {
        try {
            teacherDetailsService.deleteLessonPlan(lessonPlanId, schoolId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Lesson plan deleted successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "Failed to delete lesson plan."
            ));
        }
    }




    
}
