package com.synectiks.school.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.service.HODService;
import com.synectiks.school.service.HRService;


@RestController
@CrossOrigin
public class HRController {
	
	@Autowired
    private HRService hrDetailsService;
	
	 @PostMapping("/{schoolId}/{uid}/addHr")
	    public ResponseEntity<String> addHod(@PathVariable String schoolId,
	                                         @PathVariable String uid,
	                                         @RequestBody Map<String, Object> requestBody) {
	        try {
//	            String Department = (String) requestBody.get("Department");
	            Map<String, Object> hrDetails = (Map<String, Object>) requestBody.get("hrDetails");

	            // Call the modified addHod method with the uid
	            hrDetailsService.addHr(hrDetails, schoolId, uid);
	            return ResponseEntity.ok("HR added successfully!");
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body("Error adding HR: " + e.getMessage());
	        }
	    }

}
