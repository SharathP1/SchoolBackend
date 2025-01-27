package com.synectiks.school.controller;

import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.service.TransportServiceDetails;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class Student_Details_Controller {
	@Autowired
	private TransportServiceDetails transportServiceDetails;
	
	
	
@PostMapping("transport_details")
public List<Map<String, Object>> postMethodName(@RequestBody List<Map<String, Object>> bustransport) {
    //TODO: process POST request
	transportServiceDetails.addingTransportDetails(bustransport);

    return bustransport;
}

@PostMapping("Student_Details")
public String addingStudentDetails(@RequestBody Map<String, Object> StudentDetails) {
	String id=transportServiceDetails.addingStudent(StudentDetails);
	return id;
}

@PostMapping("Personal_Details/{sid}")
public String addingParentDetails(@RequestBody Map<String, Object> PersonalDetails,@PathVariable String sid) {
	transportServiceDetails.addingParent(PersonalDetails,sid);
	return null;
}



}
