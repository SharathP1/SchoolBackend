package com.synectiks.school.entity;

import java.util.List;

public class AttendanceDetails {
    private String sid;
    private String schoolId;
    private String sname;
    private String studentClass;
    private List<AttendanceRecord> attendance;

    // Getters and setters
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
}

