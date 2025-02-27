package com.synectiks.school.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.entity.StudentAttendance;
import com.synectiks.school.entity.TeacherAttendance;
import com.synectiks.school.service.AttendanceService;


@RestController
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	  @PostMapping("/student-attendance")
	    public ResponseEntity<String> studentAttendance(@RequestBody List<StudentAttendance> attendance) {
	        try {
	            String status = attendanceService.storeAttendanceDetails(attendance);
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
	
	
	 @GetMapping("/allSudentsAttendance/{schoolId}")
	    public List<Map<String, Object>> getAllAttendanceData(@PathVariable String schoolId) {
	        return attendanceService.getAllAttendanceData(schoolId);
	    }

	    @GetMapping("/Studentattendancebyclass/schoolId/{schoolId}/studentClass/{studentClass}")
	    public List<Map<String, Object>> filterAttendanceData(@PathVariable String schoolId,@PathVariable String studentClass) {
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
