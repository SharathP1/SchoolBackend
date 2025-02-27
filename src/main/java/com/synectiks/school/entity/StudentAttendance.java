package com.synectiks.school.entity;

import java.util.List;

public class StudentAttendance {
	private String sname;
    private String studentClass;
    private String schoolId;
    private List<AttendanceRecord> attendance;
    private String sid;

    // Getters and Setters
    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public List<AttendanceRecord> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<AttendanceRecord> attendance) {
        this.attendance = attendance;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
    

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	@Override
	public String toString() {
		return "StudentAttendance [sname=" + sname + ", studentClass=" + studentClass + ", schoolId=" + schoolId
				+ ", attendance=" + attendance + ", sid=" + sid + "]";
	}

	
    
}
