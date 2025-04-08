package com.synectiks.school.controller;

import com.synectiks.school.service.HODService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
public class HODController {

    @Autowired
    private HODService hodDetailsService;

    // Add a new HOD
    @PostMapping("/{schoolId}/{uid}/addHod")
    public ResponseEntity<String> addHod(@PathVariable String schoolId,
                                         @PathVariable String uid,
                                         @RequestBody Map<String, Object> requestBody) {
        try {
            String Department = (String) requestBody.get("Department");
            Map<String, Object> hodDetails = (Map<String, Object>) requestBody.get("hodDetails");

            // Call the modified addHod method with the uid
            hodDetailsService.addHod(hodDetails, Department, schoolId, uid);
            return ResponseEntity.ok("HOD added successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding HOD: " + e.getMessage());
        }
    }


    // Get all HODs for a school
    @GetMapping("/{schoolId}/getAllHodDetails")
    public List<Map<String, Object>> getAllHodDetails(@PathVariable String schoolId) throws InterruptedException, ExecutionException {
        return hodDetailsService.getAllHodDetails(schoolId);
    }

    // Get HOD details by ID
    @GetMapping("/getHodDetailsById/{schoolId}/{id}")
    public Map<String, Object> getHodDetailsById(@PathVariable String id, @PathVariable String schoolId) throws InterruptedException, ExecutionException {
        return hodDetailsService.getHodDetailsById(id, schoolId);
    }

    // Get HOD details by department
    @GetMapping("/{schoolId}/getHodDetailsByDepartment")
    public List<Map<String, Object>> getHodDetailsByDepartment(@RequestParam String department, @PathVariable String schoolId) throws InterruptedException, ExecutionException {
        return hodDetailsService.getHodDetailsByDepartment(department, schoolId);
    }

    // Get all teachers under an HOD by department
    @GetMapping("/{schoolId}/getTeachersByDepartment")
    public List<Map<String, Object>> getTeachersByDepartment(@RequestParam String department, @PathVariable String schoolId) throws InterruptedException, ExecutionException {
        return hodDetailsService.getTeachersByDepartment(department, schoolId);
    }

    // Approve or reject a lesson plan
    @PutMapping("school/{schoolId}/lessonPlan/{lessonPlanId}/teacher/{teacherId}/hod/{hodId}/status/{status}")
    public ResponseEntity<Map<String, String>> approveOrRejectLessonPlan(
            @PathVariable String lessonPlanId,
            @PathVariable String teacherId,
            @PathVariable String schoolId,
            @PathVariable String hodId,
            @PathVariable String status) {
        try {
            hodDetailsService.approveOrRejectLessonPlan(lessonPlanId, teacherId, schoolId, hodId, status);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Lesson plan " + status.toLowerCase() + " successfully!");
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update lesson plan status.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get all pending lesson plans for a department
    @GetMapping("/{schoolId}/getLessonPlansForApproval")
    public List<Map<String, Object>> getLessonPlansForApproval(@RequestParam String department, @PathVariable String schoolId) throws InterruptedException, ExecutionException {
        return hodDetailsService.getLessonPlansForApproval(department, schoolId);
    }
    
    @GetMapping("/getLessonPlansofallteachers/{hodId}/{schoolId}")
    public List<Map<String, Object>> getLessonPlan(@PathVariable String hodId, @PathVariable String schoolId) throws InterruptedException, ExecutionException {
        return hodDetailsService.getLessonPlan(hodId, schoolId);
    }
}