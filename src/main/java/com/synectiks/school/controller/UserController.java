package com.synectiks.school.controller;
 
import com.synectiks.school.service.UserService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.HashMap;
import java.util.Map;
 
@RestController
@RequestMapping("/api/usersinfo")
public class UserController {
 
    private final UserService userService;
 
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
 
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserByUserId(@PathVariable String userId) {
        try {
            Map<String, Object> user = userService.getUserByUserId(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}