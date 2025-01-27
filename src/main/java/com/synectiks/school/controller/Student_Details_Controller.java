package com.synectiks.school.controller;

import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.service.TransportServiceDetails;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

@PostMapping("Teacher_Details")
public String addingTeacherDetails(@RequestBody Map<String, Object> TeacherDetails) {
	transportServiceDetails.addingTeacher(TeacherDetails);
	return null;
}



}
