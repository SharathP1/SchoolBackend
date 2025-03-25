package com.synectiks.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
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