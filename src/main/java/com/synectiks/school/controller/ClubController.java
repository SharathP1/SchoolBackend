package com.synectiks.school.controller;
 
import com.synectiks.school.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/clubs")
public class ClubController {
 
    private final ClubService clubService;
 
    @Autowired
    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }
 
    @PostMapping("/{schoolId}/create")
    public ResponseEntity<Map<String, Object>> createClub(
            @PathVariable String schoolId,
            @RequestParam String clubName,
            @RequestParam String createdBy) {
        try {
            String clubId = clubService.createClub(schoolId, clubName, createdBy);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Club created successfully.");
            response.put("clubId", clubId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error creating club: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/{schoolId}/all")
    public ResponseEntity<List<Map<String, Object>>> getAllClubs(@PathVariable String schoolId) {
        try {
            List<Map<String, Object>> clubs = clubService.getAllClubs(schoolId);
            return new ResponseEntity<>(clubs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/{schoolId}/{clubId}")
    public ResponseEntity<Map<String, Object>> getClub(
            @PathVariable String schoolId,
            @PathVariable String clubId) {
        try {
            Map<String, Object> club = clubService.getClub(schoolId, clubId);
            return new ResponseEntity<>(club, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Or other appropriate status
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
 
    @PutMapping("/{schoolId}/{clubId}")
    public ResponseEntity<Map<String, Object>> updateClub(
            @PathVariable String schoolId,
            @PathVariable String clubId,
            @RequestParam String clubName) {
        try {
            clubService.updateClub(schoolId, clubId, clubName);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Club updated successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating club: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
 
    @DeleteMapping("/{schoolId}/{clubId}")
    public ResponseEntity<Map<String, Object>> deleteClub(
            @PathVariable String schoolId,
            @PathVariable String clubId) {
        try {
            clubService.deleteClub(schoolId, clubId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Club deleted successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting club: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @PutMapping("/{schoolId}/{clubId}/addStudents")
    public ResponseEntity<Map<String, Object>> addStudentsToClub(
            @PathVariable String schoolId,
            @PathVariable String clubId,
            @RequestBody List<String> studentIds) {
        try {
            clubService.addStudentsToClub(schoolId, clubId, studentIds);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Students added to club successfully.");
            response.put("clubId", clubId);
            response.put("studentIds", studentIds);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error adding students to club: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
 
    @PutMapping("/{schoolId}/{clubId}/removeStudent")
    public ResponseEntity<Map<String, Object>> removeStudentFromClub(
            @PathVariable String schoolId,
            @PathVariable String clubId,
            @RequestParam String studentId) {
        try {
            clubService.removeStudentFromClub(schoolId, clubId, studentId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Student removed from club successfully.");
            response.put("clubId", clubId);
            response.put("studentId", studentId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error removing student from club: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @PutMapping("/{schoolId}/{clubId}/updateScore")
    public ResponseEntity<Map<String, Object>> updateClubScore(
            @PathVariable String schoolId,
            @PathVariable String clubId,
            @RequestParam Long score) {
        try {
            clubService.updateClubScore(schoolId, clubId, score);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Club score updated successfully.");
            response.put("clubId", clubId);
            response.put("newScore", score);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating club score: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/{schoolId}/{clubId}/getScore")
    public ResponseEntity<Long> getClubScore(
            @PathVariable String schoolId,
            @PathVariable String clubId) {
        try {
            Long score = clubService.getClubScore(schoolId, clubId);
            return new ResponseEntity<>(score, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/{schoolId}/student/{studentId}/clubs")
    public ResponseEntity<List<Map<String, Object>>> getClubsByStudentId(
            @PathVariable String schoolId,
            @PathVariable String studentId) {
        try {
            List<Map<String, Object>> clubs = clubService.getClubsByStudentId(schoolId, studentId);
            return new ResponseEntity<>(clubs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/{schoolId}/{clubId}/exists")
    public ResponseEntity<Boolean> isClubCreated(
            @PathVariable String schoolId,
            @PathVariable String clubId) {
        try {
            boolean exists = clubService.isClubCreated(schoolId, clubId);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}