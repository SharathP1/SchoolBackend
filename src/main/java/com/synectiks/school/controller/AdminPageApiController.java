package com.synectiks.school.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.entity.DeleteMarksRequest;
import com.synectiks.school.entity.FeeDetails;
import com.synectiks.school.entity.StudentFeeDetails;
import com.synectiks.school.entity.StudentMarks;
import com.synectiks.school.entity.Term;
import com.synectiks.school.service.AdminPageApiService;

@RestController
public class AdminPageApiController {

    @Autowired
    private AdminPageApiService adminPageApiService;

    @PostMapping("/addFeeDetails/{schoolId}/{id}")
    public String addFeeDetails(@RequestBody StudentFeeDetails transactionDetails, @PathVariable String schoolId, @PathVariable String id) {
        return adminPageApiService.addFeeDetails(transactionDetails, schoolId, id);
    }

    @PostMapping("/updateFeeDetailsBySid/{schoolId}")
    public void updateFeeDetailsBySid(@RequestParam String sid, @PathVariable String schoolId, @RequestBody List<Map<String, Object>> newFeeDetails) {
        adminPageApiService.updateFeeDetailsBySid(sid, schoolId, newFeeDetails);
    }

    @GetMapping("/getFeeDetails/{schoolId}/{id}")
    public ResponseEntity<?> getAllFeeDetails(@PathVariable String schoolId, @PathVariable String id) {
        return adminPageApiService.getAllFeeDetails(schoolId, id);
    }

    // 2. Get fee details for the current month
    @GetMapping("/getMonthlyFeeDetails/{schoolId}/{id}")
    public ResponseEntity<?> getMonthlyFeeDetails(@PathVariable String schoolId, @PathVariable String id) {
        return adminPageApiService.getMonthlyFeeDetails(schoolId, id);
    }

    // 3. Get fee details for the current week
    @GetMapping("/getWeeklyFeeDetails/{schoolId}/{id}")
    public ResponseEntity<?> getWeeklyFeeDetails(@PathVariable String schoolId, @PathVariable String id) {
        return adminPageApiService.getWeeklyFeeDetails(schoolId, id);
    }

    @GetMapping("/getTotalPaymentsReceived/{schoolId}")
    public double getTotalPaymentsReceived(@PathVariable String schoolId) {
        return adminPageApiService.getTotalPaymentsReceived(schoolId);
    }

    @GetMapping("/getAllPaymentsWithPaidDate/{schoolId}")
    public List<Map<String, Object>> getAllPaymentsWithPaidDate(@PathVariable String schoolId) {
        return adminPageApiService.getAllPaymentsWithPaidDate(schoolId);
    }

    @GetMapping("/getFeeDetailsForCurrentWeek/{schoolId}")
    public List<Map<String, Object>> getFeeDetailsForCurrentWeek(@PathVariable String schoolId) {
        return adminPageApiService.getFeeDetailsForCurrentWeek(schoolId);
    }

    @GetMapping("/getTotalPaymentsReceivedToday/{schoolId}")
    public double getTotalPaymentsReceivedToday(@PathVariable String schoolId) {
        return adminPageApiService.getTotalPaymentsReceivedToday(schoolId);
    }

    @GetMapping("/getFeeDetailsForNextMonth/{schoolId}")
    public List<com.synectiks.school.service.FeeDetails> getFeeDetailsForNextMonth(@PathVariable String schoolId, @RequestParam String sid) throws InterruptedException, ExecutionException {
        return adminPageApiService.getFeeDetailsForNextMonth(schoolId, sid);
    }

    @GetMapping("/getFeeDetailsForPreviousMonth/{schoolId}")
    public List<com.synectiks.school.service.FeeDetails> getFeeDetailsForPreviousMonth(@PathVariable String schoolId, @RequestParam String sid) throws InterruptedException, ExecutionException {
        return adminPageApiService.getFeeDetailsForPreviousMonth(schoolId, sid);
    }

    // Term Management Endpoints
    @PostMapping("/addterm/{schoolId}")
    public ResponseEntity<String> addTermToCurriculum(
            @PathVariable String schoolId,
            @RequestBody Map<String, Object> termData
    ) {
        try {
            String termId = adminPageApiService.addTerm(schoolId, termData);
            return ResponseEntity.ok(termId);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(500).body("Error adding term: " + e.getMessage());
        }
    }


    @GetMapping("/getterms/{schoolId}")
    public List<Term> getTerms(@PathVariable String schoolId) throws InterruptedException, ExecutionException {
        return adminPageApiService.getTerms(schoolId);
    }


    @DeleteMapping("/deleteterm/{schoolId}/{termId}")
    public void deleteTerm(@PathVariable String schoolId, @PathVariable String termId) throws InterruptedException, ExecutionException {
        adminPageApiService.deleteTerm(schoolId, termId);
    }

    
    @DeleteMapping("/deleteAssessment/{schoolId}/{termId}/{assessmentId}")
    public void deleteAssessment(
            @PathVariable String schoolId,
            @PathVariable String termId,
            @PathVariable String assessmentId
    ) throws InterruptedException, ExecutionException {
        adminPageApiService.deleteAssessment(schoolId, termId, assessmentId);
    }
    
    @PostMapping("/enterMarks/{schoolId}/{termId}/{testId}/{teacherId}/{className}")
    public ResponseEntity<String> enterMarks(
            @PathVariable String schoolId,
            @PathVariable String termId,
            @PathVariable String testId,
            @PathVariable String teacherId,
            @PathVariable String className,
            @RequestBody List<StudentMarks> studentMarksList
    ) {
        try {
            adminPageApiService.enterMarks(schoolId, termId, testId, teacherId, className, studentMarksList);
            return ResponseEntity.ok("Marks entered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error entering marks: " + e.getMessage());
        }
    }
    
    @PostMapping("/updateMarks/{schoolId}/{termId}/{testId}/{teacherId}/{className}")
    public ResponseEntity<Object> updateMarks(
            @PathVariable String schoolId,
            @PathVariable String termId,
            @PathVariable String testId,
            @PathVariable String teacherId,
            @PathVariable String className,
            @RequestBody List<StudentMarks> updatedMarksList) {
        try {
            adminPageApiService.updateMarks(schoolId, termId, testId, teacherId, className, updatedMarksList);
            return ResponseEntity.ok("Marks updated successfully for " + updatedMarksList.size() + " students");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating marks: " + e.getMessage());
        }
    }


    
    @GetMapping("/viewMarksByClass/{schoolId}/{termId}/{testId}/{className}")
    public ResponseEntity<Object> viewMarksByClass(
            @PathVariable String schoolId,
            @PathVariable String termId,
            @PathVariable String testId,
            @PathVariable String className
    ) {
        try {
            List<StudentMarks> marksList = adminPageApiService.viewMarksByClass(schoolId, termId, testId, className);
            return ResponseEntity.ok(marksList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving marks: " + e.getMessage());
        }
    }

    
    @GetMapping("/viewMarksByStudent/{schoolId}/{termId}/{testId}/{studentId}")
    public ResponseEntity<Object> viewMarksByStudent(
            @PathVariable String schoolId,
            @PathVariable String termId,
            @PathVariable String testId,
            @PathVariable String studentId
    ) {
        try {
            List<StudentMarks> marksList = adminPageApiService.viewMarksByStudent(schoolId, termId, testId, studentId);
            return ResponseEntity.ok(marksList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving marks: " + e.getMessage());
        }
    }

    
    @GetMapping("/viewMarksBySubject/{schoolId}/{termId}/{testId}/{subject}")
    public ResponseEntity<Object> viewMarksBySubject(
            @PathVariable String schoolId,
            @PathVariable String termId,
            @PathVariable String testId,
            @PathVariable String subject
    ) {
        try {
            List<StudentMarks> marksList = adminPageApiService.viewMarksBySubject(schoolId, termId, testId, subject);
            return ResponseEntity.ok(marksList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving marks: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/deleteMarks/{schoolId}/{termId}/{testId}/{teacherId}/{className}")
    public ResponseEntity<Object> deleteMarks(
            @PathVariable String schoolId,
            @PathVariable String termId,
            @PathVariable String testId,
            @PathVariable String teacherId,
            @PathVariable String className,
            @RequestBody List<DeleteMarksRequest> deleteRequests) {
        try {
            adminPageApiService.deleteMarks(schoolId, termId, testId, teacherId, className, deleteRequests);
            return ResponseEntity.ok("Marks deleted successfully for " + deleteRequests.size() + " entries");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting marks: " + e.getMessage());
        }
    }



}
