package com.synectiks.school.controller;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.synectiks.school.service.FirebaseAuthService;


@RestController
public class AuthController {
    
	
	 @Autowired
	    private FirebaseAuthService firebaseAuthService;

	    @PostMapping("/signup")
	    public Map<String, String> signUp(@RequestBody Map<String, String> user) {
	        try {
	            String schoolName = user.get("schoolName");
	            String email = user.get("email");
	            String location = user.get("location");
	            String password = user.get("password");

	            return firebaseAuthService.signUp(schoolName, email, location, password);
	        } catch (Exception e) {
	            e.printStackTrace();
	            Map<String, String> errorResponse = new HashMap<>();
	            errorResponse.put("error", "Failed to register school: " + e.getMessage());
	            return errorResponse;
	        }
	    }

	    @PostMapping("/signin")
	    public Map<String, String> signIn(@RequestBody Map<String, String> user) {
	        try {
	            String email = user.get("email");
	            String password = user.get("password");
	            return firebaseAuthService.signIn(email, password);
	        } catch (Exception e) {
	            e.printStackTrace();
	            Map<String, String> errorResponse = new HashMap<>();
	            errorResponse.put("error", "Failed to sign in: " + e.getMessage());
	            return errorResponse;
	        }
	    }
     

	    
	    @PostMapping("/create-parents")
	    public Map<String, String> createParents( @RequestBody Map<String, Object> request) {
	        try {
	        	
	            
	            String schoolId = (String) request.get("schoolId");
	            if (schoolId == null || schoolId.isEmpty()) {
	                return Map.of("error", "School ID is required");
	            }

	            java.util.List<Map<String, String>> parents = (java.util.List<Map<String, String>>) request.get("parents");
	            if (parents == null || parents.isEmpty()) {
	                return Map.of("error", "Parent data is required");
	            }

	            return firebaseAuthService.createParentAccounts(schoolId, parents);
	        } catch (Exception e) {
	            return Map.of("error", "Failed to process request: " + e.getMessage());
	        }
	    }


    
}