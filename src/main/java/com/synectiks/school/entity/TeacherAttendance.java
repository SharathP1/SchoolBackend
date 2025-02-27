package com.synectiks.school.entity;

import java.util.List;

public class TeacherAttendance {
	private String employeeId;
    private String name;
    private String schoolId;
    private List<AttendanceRecord> attendance;


    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AttendanceRecord> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<AttendanceRecord> attendance) {
        this.attendance = attendance;
    }

    public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	@Override
	public String toString() {
		return "TeacherAttendance [employeeId=" + employeeId + ", name=" + name + ", schoolId=" + schoolId
				+ ", attendance=" + attendance + "]";
	}

	
}
