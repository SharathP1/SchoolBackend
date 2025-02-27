package com.synectiks.school.controller;

import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.entity.BusRoute;
import com.synectiks.school.entity.StudentDetails;
import com.synectiks.school.entity.StudentFeeDetails;
import com.synectiks.school.service.AttendanceDetails;
import com.synectiks.school.service.FeeDetails;
import com.synectiks.school.service.PersonalDetails;
import com.synectiks.school.service.StudentsDetails;
import com.synectiks.school.service.TeacherDetails;
import com.synectiks.school.service.TransportServiceDetails;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@CrossOrigin
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
	@Autowired
	private TeacherDetails teacherDetails;
	
	
	//POST Methods
@PostMapping("transport_details/{schoolId}")
public void postMethodName(@RequestBody List<BusRoute> bustransport,@PathVariable String schoolId) {
    //TODO: process POST request
	transportServiceDetails.addingTransportDetails(bustransport,schoolId);
  
}

@PostMapping("Student_Details/{schoolId}")
public String addingStudentDetails(@RequestBody StudentDetails studentDetails,@PathVariable String schoolId) {
	String id=studentsDetails.addingStudent(studentDetails,schoolId);
	return id;
}

//@PostMapping("Personal_Details/{sid}")
//public String addingParentDetails(@RequestBody Map<String, Object> PersonalDetails,@PathVariable String sid) {
//	personalServiceDetails.addingParent(PersonalDetails,sid);
//	return null;
//}

//@PostMapping("Fee_Details/{sid}")
//public String addingFeeDetails(@RequestBody Map<String, Object> FeeDetails,@PathVariable String sid) {
//	feeDetails.addingFee(FeeDetails,sid);
//	return "Fee Details recorded successfully!";
//}

@PostMapping("attendance/{sid}")
public String markAttendance(@RequestBody Map<String, Object> attendanceDetail, @PathVariable String sid) {
	attendanceDetails.markAttendance(attendanceDetail, sid);
    return "Attendance recorded successfully!";
}

@PostMapping("student_attendance_per_period")
public String student_attendance_per_period(@RequestBody Map<String, Object> student_attendance_per_period) {
    attendanceDetails.student_attendance_per_period(student_attendance_per_period);
    return "Attendance recorded successfully!";
}





     //Get Methods

@GetMapping("get_StudentDetails/{schoolId}")
public List<StudentDetails> getMethodName(@PathVariable String schoolId) throws InterruptedException, ExecutionException {
	List<StudentDetails> studentdetails= studentsDetails.getstudentdetails(schoolId);
    return studentdetails ;
}


@GetMapping("get_Student_Details_by_id/schoolId/{schoolId}/StudentrollNumber/{rollNumber}")
public Map<String, Object> getStudentById(@PathVariable String schoolId,@PathVariable String rollNumber) throws ExecutionException, InterruptedException {
    return studentsDetails.getStudentById(schoolId,rollNumber);
}
//
//@GetMapping("get_Student_Details/{id}/transport")
//public Map<String, Object> getTransportDetailsByStudentId(@PathVariable String id) throws ExecutionException, InterruptedException {
//    Map<String, Object> studentDetails = studentsDetails.getStudentById(id);
//    System.out.println(studentDetails);
//    if (studentDetails != null && studentDetails.containsKey("Route_Name")) {
//    	System.out.println("#########");
//        String routeName = (String) studentDetails.get("Route_Name");
//        System.out.println(routeName);
//        return studentsDetails.getTransportDetailsByRouteName(routeName);
//    } else {
//        return null;
//    }
//}


@GetMapping("get_Student_Details_By_Class/{schoolId}")
public List<StudentDetails> getMethodNamebyClass(@RequestParam String Class,@PathVariable String schoolId) throws InterruptedException, ExecutionException {
    List<StudentDetails> studentdetails = studentsDetails.getstudentdetailsbyClass(Class,schoolId);
    return studentdetails;
}
 
//@GetMapping("get_Student_Details_By_Class_And_Section")
//public List<Map<String, Object>> getstudentdetailsbyClassandSection(@RequestParam String class1, @RequestParam String section) throws InterruptedException, ExecutionException {
//    List<Map<String, Object>> studentdetails = studentsDetails.getstudentdetailsbyclassandsection(class1, section);
//    return studentdetails;
//}

//@GetMapping("get_Personal_Details")
//public List<Map<String, Object>> getMethodName1() throws InterruptedException, ExecutionException {
//	List<Map<String, Object>> personaldetails= personalServiceDetails.getpersonaldetails();
//    return personaldetails ;
//}
// 
//@GetMapping("get_Personal_Details/{sid}")
//public List<Map<String, Object>> getMethodNamebyid(@PathVariable String sid) throws InterruptedException, ExecutionException {
//	List<Map<String, Object>> personaldetails= personalServiceDetails.getpersonaldetailsById(sid);
//    return personaldetails ;
//}

//@GetMapping("get_Fee_Details")
//public List<Map<String, Object>> getFeeDetails(@RequestParam String sid) throws InterruptedException, ExecutionException {
//	System.out.println("#########");
//    return feeDetails.getFeeDetails(sid);
//}

@GetMapping("get_Fee_Details_by_class/school/{schoolId}")
public List<StudentFeeDetails> getFeeDetails1(@RequestParam String clas,@PathVariable String schoolId) throws InterruptedException, ExecutionException {
	
    return feeDetails.getFeeDetails1(clas,schoolId);
}


@GetMapping("attendance/school/{schoolId}/studentId/{sid}")
public List<Map<String, Object>> getAttendance(@PathVariable String schoolId,@PathVariable String sid) throws InterruptedException, ExecutionException {
    return attendanceDetails.getAttendance(schoolId,sid);
}

@GetMapping("attendance_by_class/{schoolId}")
public List<Map<String, Object>> getAttendance1(@PathVariable String schoolId, @RequestParam String clas) throws InterruptedException, ExecutionException {
    return attendanceDetails.getAttendance1(schoolId,clas);
}


@GetMapping("student_attendance_per_period/class_section_name_period")
public List<Map<String, Object>> getAttendanceByClassSectionNamePeriod(
        @RequestParam(required = false) String classNumber,
        @RequestParam(required = false) String section,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String period) throws InterruptedException, ExecutionException {
    return attendanceDetails.getAttendanceByClassSectionNamePeriod(classNumber, section, name, period);
}

}
