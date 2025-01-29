package com.synectiks.school.controller;

import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.service.TransportServiceDetails;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class Student_Details_Controller {
	@Autowired
	private TransportServiceDetails transportServiceDetails;
	
	
	
	//POST Methods
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

@PostMapping("Fee_Details/{sid}")
public String addingFeeDetails(@RequestBody Map<String, Object> FeeDetails,@PathVariable String sid) {
	transportServiceDetails.addingFee(FeeDetails,sid);
	return null;
}


     //Get Methods

@GetMapping("get_Student_Details")
public List<Map<String, Object>> getMethodName() throws InterruptedException, ExecutionException {
	List<Map<String, Object>> studentdetails= transportServiceDetails.getstudentdetails();
    return studentdetails ;
}

@GetMapping("get_Personal_Details")
public List<Map<String, Object>> getMethodName1() throws InterruptedException, ExecutionException {
	List<Map<String, Object>> personaldetails= transportServiceDetails.getpersonaldetails();
    return personaldetails ;
}

@GetMapping("get_Fee_Details")
public List<Map<String, Object>> getMethodName2() throws InterruptedException, ExecutionException {
	List<Map<String, Object>> Feedetails= transportServiceDetails.getfeedetails();
    return Feedetails ;
}

}
