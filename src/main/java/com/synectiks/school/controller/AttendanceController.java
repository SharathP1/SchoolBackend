package com.synectiks.school.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.service.AttendanceService;


@RestController
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	
	
	 @PostMapping("/attendance")
	    public List<Map<String, Object>> studentAttendance(@RequestBody List<Map<String, Object>> attendance) {
	        try {
	            attendanceService.storeAttendanceDetails(attendance);
	            return attendance;
	        } catch (Exception e) {
	            // Handle the exception (e.g., log it, return an error response)
	            e.printStackTrace();
	            throw new RuntimeException("Failed to store attendance details");
	        }
	    }
	
	@PostMapping("/teacher-attendance")
	public Map<String, Object> studentAttendance1(@RequestBody Map<String, Object> attendance) {
		
		attendanceService.storeTeacherAttendanceDetails(attendance);
		return attendance;
	}
	
	
	 @GetMapping("/allSudentsAttendance")
	    public List<Map<String, Object>> getAllAttendanceData() {
	        return attendanceService.getAllAttendanceData();
	    }

	    @GetMapping("/Studentattendancefilter")
	    public List<Map<String, Object>> filterAttendanceData(
	    		@RequestParam(required = false) String name,
	    		@RequestParam(required = false) String sid,
	            @RequestParam(required = false) String period,
	            @RequestParam(required = false) String time) {
	        List<Map<String, Object>> allData = attendanceService.getAllAttendanceData();
	        return attendanceService.filterAttendanceData(allData, name, period, time);
	    }
	    
	    @GetMapping("/allTeacherAttendance")
	    public List<Map<String, Object>> getAllTeacherAttendanceData() {
	        return attendanceService.getAllTeacherAttendanceData();
	    }

	    @GetMapping("/teacherAttendancefilter")
	    public List<Map<String, Object>> filterTeacherAttendanceData(@RequestParam(required = false) String time, String name) {
	        List<Map<String, Object>> allData = attendanceService.getAllTeacherAttendanceData();
	        return attendanceService.filterTeacherAttendanceData(allData, time, name);
	    }
	

}
