package com.synectiks.school.entity;

import java.util.List;

public class StudentAttendance {
    private String sid;
    private String sname;
    private String period;
    private String studentClass;
    private String schoolId;
    private List<AttendanceRecord> attendance;

    // Getters and setters
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
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
}
