package com.synectiks.school.entity;

import java.util.List;

public class StudentFeeDetails {

    private String rollNo;
    private String studentClass;
    private String schoolId;
    private String name;
    private String sid;
    private List<FeeDetails> feeDetails;
    public StudentFeeDetails() {
		
	}

//    // Constructor
//    public StudentFeeDetails(String rollNo, String studentClass, String schoolId, String name, String sid, List<FeeDetails> feeDetails) {
//        this.rollNo = rollNo;
//        this.studentClass = studentClass;
//        this.schoolId = schoolId;
//        this.name = name;
//        this.sid = sid;
//        this.feeDetails = feeDetails;
//    }

    // Getters and Setters
    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public List<FeeDetails> getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(List<FeeDetails> feeDetails) {
        this.feeDetails = feeDetails;
    }

    @Override
    public String toString() {
        return "StudentFeeDetails{" +
                "rollNo='" + rollNo + '\'' +
                ", studentClass='" + studentClass + '\'' +
                ", schoolId='" + schoolId + '\'' +
                ", name='" + name + '\'' +
                ", sid='" + sid + '\'' +
                ", feeDetails=" + feeDetails +
                '}';
    }
}
