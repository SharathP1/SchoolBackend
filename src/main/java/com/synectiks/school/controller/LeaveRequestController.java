package com.synectiks.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.synectiks.school.service.LeaveRequestService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/{schoolId}/{studentId}/{teacherId}")
    public String sendLeaveRequest(
            @PathVariable String schoolId,
            @PathVariable String studentId,
            @PathVariable String teacherId,
            @RequestBody Map<String, Object> leaveDetails) throws ExecutionException, InterruptedException {
        return leaveRequestService.sendLeaveRequest(schoolId, studentId, teacherId, leaveDetails);
    }
    
    @PostMapping("/{schoolId}/{hodId}/{teacherId}/teacher")
    public String sendTeacherLeaveRequest(
            @PathVariable String schoolId,
            @PathVariable String hodId,
            @PathVariable String teacherId,
            @RequestBody Map<String, Object> leaveDetails) throws ExecutionException, InterruptedException {
        return leaveRequestService.sendTeacherLeaveRequest(schoolId, teacherId, hodId, leaveDetails);
    }
    
    @PostMapping("/{schoolId}/{hrId}/{hodId}/hod")
    public ResponseEntity<String> sendHODLeaveRequest(
            @PathVariable String schoolId,
            @PathVariable String hrId,
            @PathVariable String hodId,
            @RequestBody Map<String, Object> leaveDetails) {
        try {
            String result = leaveRequestService.sendHODLeaveRequest(schoolId, hodId, hrId, leaveDetails);
            return ResponseEntity.ok(result);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing leave request");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{schoolId}/{teacherId}/{studentId}/{action}")
    public String processLeaveRequest(
            @PathVariable String schoolId,
            @PathVariable String teacherId,
            @PathVariable String studentId,
            @PathVariable String action) {
        try {
            return leaveRequestService.processLeaveRequest(teacherId, studentId, action);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error processing leave request", e);
        }
    }
    
    @PostMapping("/{schoolId}/{hodId}/{teacherId}/teacher/{action}")
    public String hodprocessLeaveRequest(
            @PathVariable String schoolId,
            @PathVariable String hodId,
            @PathVariable String teacherId,
            @PathVariable String action) {
        try {
            return leaveRequestService.HodprocessLeaveRequest(schoolId, hodId, teacherId, action);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error processing leave request", e);
        }
    }
    
    @PostMapping("/{schoolId}/{hrId}/{hodId}/hod/{action}")
    public String hrprocessLeaveRequest(
            @PathVariable String schoolId,
            @PathVariable String hrId,
            @PathVariable String hodId,
            @PathVariable String action) {
        try {
            return leaveRequestService.HRprocessLeaveRequest(schoolId, hrId, hodId, action);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error processing leave request", e);
        }
    }



    @GetMapping("/{schoolId}/{teacherId}/view")
    public List<Map<String, Object>> viewLeaveRequests(
            @PathVariable String schoolId,
            @PathVariable String teacherId,
            @RequestParam(required = false) String studentId) {
        try {
            return leaveRequestService.viewLeaveRequests(teacherId, studentId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error viewing leave requests", e);
        }
    }
    
    @GetMapping("/{schoolId}/{hodId}/teacher/view")
    public List<Map<String, Object>> viewTeacherLeaveRequests(
    		@PathVariable String schoolId,
            @PathVariable String hodId,
            @RequestParam(required = false) String teacherId) {
        try {
            return leaveRequestService.viewTeacherLeaveRequests(hodId, teacherId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching leave requests", e);
        }
    }
    
    @GetMapping("/{schoolId}/{hrId}/hod/view")
    public List<Map<String, Object>> viewHODLeaveRequests(
    		@PathVariable String schoolId,
            @PathVariable String hrId,
            @RequestParam(required = false) String hodId) {
        try {
            return leaveRequestService.viewHODLeaveRequests(hrId, hodId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching leave requests", e);
        }
    }
    
    @GetMapping("/{schoolId}/{hodId}/{teacherId}/teacher-leaves")
    public List<Map<String, Object>> viewLeaveRequestsbyteacherId(
            @PathVariable String schoolId,
            @PathVariable String hodId,
            @PathVariable String teacherId) {
        try {
            return leaveRequestService.viewLeaveRequestsbyteacherId(teacherId, hodId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error viewing leave requests", e);
        }
    }
    
    @GetMapping("/{schoolId}/{hrId}/{hodId}/hod-leaves")
    public List<Map<String, Object>> viewLeaveRequestsbyhodId(
            @PathVariable String schoolId,
            @PathVariable String hodId,
            @PathVariable String hrId) {
        try {
            return leaveRequestService.viewLeaveRequestsbyhodId(hrId, hodId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error viewing leave requests", e);
        }
    }
    
    
    

    @GetMapping("/{schoolId}/{teacherId}/{studentId}/view")
    public List<Map<String, Object>> viewLeaveRequestsbystudentId(
            @PathVariable String schoolId,
            @PathVariable String teacherId,
            @PathVariable String studentId) {
        try {
            return leaveRequestService.viewLeaveRequestsbyStudentId1(teacherId, studentId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error viewing leave requests", e);
        }
    }
    
    

    @GetMapping("/{schoolId}/{teacherId}/all")
    public List<Map<String, Object>> getAllLeaveRequestsForTeacher(
            @PathVariable String schoolId,
            @PathVariable String teacherId) {
        try {
            return leaveRequestService.getAllLeaveRequestsForTeacher(schoolId, teacherId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching all leave requests", e);
        }
    }
}