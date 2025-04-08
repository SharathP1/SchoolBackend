package com.synectiks.school.service;

import java.util.List;

public class TeacherAttendanceDetails {
    private String tid;
    private String tname;
    private String schoolId;
    private List<AttendanceRecord> attendance;

    // Constructor
    public TeacherAttendanceDetails(String tid, String tname, String schoolId, List<AttendanceRecord> attendance) {
        this.tid = tid;
        this.tname = tname;
        this.schoolId = schoolId;
        this.attendance = attendance;
    }

    // Getters and Setters
    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public List<AttendanceRecord> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<AttendanceRecord> attendance) {
        this.attendance = attendance;
    }

	public String getHodId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setHodId(String hodId) {
		// TODO Auto-generated method stub
		
	}
}

class AttendanceRecord {
    private String period;
    private String time;
    private boolean present;

    // Constructor
    public AttendanceRecord(String period, String time, boolean present) {
        this.period = period;
        this.time = time;
        this.present = present;
    }

    // Getters and Setters
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

	public Object getDate() {
		// TODO Auto-generated method stub
		return null;
	}
}
