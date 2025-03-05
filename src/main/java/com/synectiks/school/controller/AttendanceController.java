package com.synectiks.school.controller;

import com.synectiks.school.entity.AttendanceDetails;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.entity.AttendanceDetails;
import com.synectiks.school.entity.StudentAttendance;
import com.synectiks.school.entity.TeacherAttendance;
import com.synectiks.school.service.AttendanceService;


@RestController
@CrossOrigin
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	 @PostMapping("/student-attendance/{schoolId}/{studentId}")
	    public ResponseEntity<String> storeAttendanceDetails(
	        @PathVariable String schoolId,
	        @PathVariable String studentId,
	        @RequestBody AttendanceDetails attendanceDetails) {

	        // Set the schoolId and studentId in the attendanceDetails object
	        attendanceDetails.setSchoolId(schoolId);
	        attendanceDetails.setSid(studentId);

	        String result = attendanceService.storeAttendanceDetails(attendanceDetails);
	        return ResponseEntity.ok(result);
	    }




	@PostMapping("/student-day-attendance/{schoolId}/{id}")
	public ResponseEntity<String> studentdayAttendance(
	        @PathVariable String schoolId,
	        @PathVariable String id,
	        @RequestBody List<StudentAttendance> attendance) {
	    try {
	        // Set the schoolId and id for each attendance record
	        for (StudentAttendance record : attendance) {
	            record.setSchoolId(schoolId);
	            record.setSid(id);
	        }

	        String status = attendanceService.storeDayAttendanceDetails(attendance);
	        return ResponseEntity.ok(status);
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("Error storing student attendance: " + e.getMessage());
	    }
	}


	    @PostMapping("/teacher-attendance")
	    public ResponseEntity<String> teacherAttendance(@RequestBody TeacherAttendance attendance) {
	        try {
	            String status = attendanceService.storeTeacherAttendanceDetails(attendance);
	            return ResponseEntity.ok(status);
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("Error storing teacher attendance: " + e.getMessage());
	        }
	    }
	
	
	 @GetMapping("/allStudentsAttendance/{schoolId}")
	    public List<Map<String, Object>> getAllAttendanceData(@PathVariable String schoolId) {
	        return attendanceService.getAllAttendanceData(schoolId);
	    }
	 
	 @GetMapping("/allDayStudentsAttendance/{schoolId}")
	    public List<Map<String, Object>> getAllAttendanceData1(@PathVariable String schoolId) {
	        return attendanceService.getAllDayAttendanceData(schoolId);
	    }
	 

	    @GetMapping("/oneStudentAttendance/{schoolId}/{sid}")
	    public List<Map<String, Object>> getAttendanceData(
	            @PathVariable String schoolId,
	            @PathVariable String sid) {
	        System.out.println("Received request for schoolId: " + schoolId + " and sid: " + sid);
	        List<Map<String, Object>> attendanceData = attendanceService.getAttendanceData(schoolId, sid);
	        System.out.println("Attendance data retrieved: " + attendanceData);
	        return attendanceData;
	    }
	    
	    @GetMapping("/day-wise-single-StudentAttendance/{schoolId}/{sid}")
	    public List<Map<String, Object>> getDayAttendanceData(
	            @PathVariable String schoolId,
	            @PathVariable String sid) {
	        System.out.println("Received request for schoolId: " + schoolId + " and sid: " + sid);
	        List<Map<String, Object>> attendanceData = attendanceService.getDayAttendanceData(schoolId, sid);
	        System.out.println("Attendance data retrieved: " + attendanceData);
	        return attendanceData;
	    }
	    
	    @GetMapping("/day-wise-single-StudentAttendanceDateWise/{schoolId}/{sid}/{date}")
	    public ResponseEntity<List<Map<String, Object>>> getDayAttendanceData(
	            @PathVariable String schoolId,
	            @PathVariable String sid,
	            @PathVariable String date) {

	        System.out.println("Fetching attendance for schoolId: " + schoolId + ", sid: " + sid + ", date: " + date);
	        List<Map<String, Object>> attendanceData = attendanceService.getDayAttendanceDataDateWise(schoolId, sid, date);

	        if (attendanceData.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(attendanceData);
	        }

	        return ResponseEntity.ok(attendanceData);
	    }
	    
	    @GetMapping("/month-wise-single-StudentAttendance/{schoolId}/{sid}/{month}")
	    public ResponseEntity<List<Map<String, Object>>> getMonthAttendanceData(
	            @PathVariable String schoolId,
	            @PathVariable String sid,
	            @PathVariable String month) {

	        System.out.println("Fetching month-wise attendance for schoolId: " + schoolId + ", sid: " + sid + ", month: " + month);
	        List<Map<String, Object>> attendanceData = attendanceService.getMonthAttendanceData(schoolId, sid, month);

	        if (attendanceData.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(attendanceData);
	        }

	        return ResponseEntity.ok(attendanceData);
	    }

	    @GetMapping("/Studentattendancebyclass/schoolId/{schoolId}/studentClass")
	    public List<Map<String, Object>> filterAttendanceData(@PathVariable String schoolId,@RequestParam String studentClass) {
	        List<Map<String, Object>> allData = attendanceService.getstudentAttendanceData(schoolId,studentClass);
	        return allData;
	    }
	    
	    @GetMapping("/allTeacherAttendance/{schoolId}")
	    public List<Map<String, Object>> getAllTeacherAttendanceData(@PathVariable String schoolId) {
	        return attendanceService.getAllTeacherAttendanceData(schoolId);
	    }

//	    @GetMapping("/teacherAttendancefilter")
//	    public List<Map<String, Object>> filterTeacherAttendanceData(@RequestParam(required = false) String time, String name) {
//	        List<Map<String, Object>> allData = attendanceService.getAllTeacherAttendanceData();
//	        return attendanceService.filterTeacherAttendanceData(allData, time, name);
//	    }
	

}
