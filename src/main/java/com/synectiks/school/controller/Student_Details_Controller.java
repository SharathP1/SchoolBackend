package com.synectiks.school.controller;

import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.service.AttendanceDetails;
import com.synectiks.school.service.FeeDetails;
import com.synectiks.school.service.PersonalDetails;
import com.synectiks.school.service.StudentsDetails;
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
	@Autowired
	private PersonalDetails personalServiceDetails;
	@Autowired
	private StudentsDetails studentsDetails;
	@Autowired
	private FeeDetails feeDetails;
	@Autowired
	private AttendanceDetails attendanceDetails;
	
	
	//POST Methods
@PostMapping("transport_details")
public List<Map<String, Object>> postMethodName(@RequestBody List<Map<String, Object>> bustransport) {
    //TODO: process POST request
	transportServiceDetails.addingTransportDetails(bustransport);
    return bustransport;
}

@PostMapping("Student_Details")
public String addingStudentDetails(@RequestBody Map<String, Object> StudentDetails) {
	String id=studentsDetails.addingStudent(StudentDetails);
	return id;
}

@PostMapping("Personal_Details/{sid}")
public String addingParentDetails(@RequestBody Map<String, Object> PersonalDetails,@PathVariable String sid) {
	personalServiceDetails.addingParent(PersonalDetails,sid);
	return null;
}

@PostMapping("Fee_Details/{sid}")
public String addingFeeDetails(@RequestBody Map<String, Object> FeeDetails,@PathVariable String sid) {
	feeDetails.addingFee(FeeDetails,sid);
	return "Fee Details recorded successfully!";
}

@PostMapping("attendance/{sid}")
public String markAttendance(@RequestBody Map<String, Object> attendanceDetail, @PathVariable String sid) {
	attendanceDetails.markAttendance(attendanceDetail, sid);
    return "Attendance recorded successfully!";
}


     //Get Methods

@GetMapping("get_Student_Details")
public List<Map<String, Object>> getMethodName() throws InterruptedException, ExecutionException {
	List<Map<String, Object>> studentdetails= studentsDetails.getstudentdetails();
    return studentdetails ;
}

@GetMapping("get_Personal_Details")
public List<Map<String, Object>> getMethodName1() throws InterruptedException, ExecutionException {
	List<Map<String, Object>> personaldetails= personalServiceDetails.getpersonaldetails();
    return personaldetails ;
}

@GetMapping("get_Fee_Details")
public List<Map<String, Object>> getMethodName2() throws InterruptedException, ExecutionException {
	List<Map<String, Object>> Feedetails= feeDetails.getfeedetails();
    return Feedetails ;
}

@GetMapping("attendance/{sid}")
public List<Map<String, Object>> getAttendance(@PathVariable String sid) throws InterruptedException, ExecutionException {
    return attendanceDetails.getAttendance(sid);
}


}
