package com.synectiks.school.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class StudentDetails {

    @PropertyName("Aadhaar_Number")
    private String aadhaarNumber;

    @PropertyName("Address")
    private String address;

    @PropertyName("Admission_Name")
    private String admissionName;

    @PropertyName("Age")
    private String age;

    @PropertyName("Class")
    private String studentClass;

    @PropertyName("DOB")
    private String dob;

    @PropertyName("Email")
    private String email;

    @PropertyName("Father_Name")
    private String fatherName;

    @PropertyName("Father_Occupation")
    private String fatherOccupation;

    @PropertyName("Gender")
    private String gender;

    @PropertyName("Mother_Name")
    private String motherName;

    @PropertyName("Mother_Occupation")
    private String motherOccupation;

    @PropertyName("Phone_Number")
    private String phoneNumber;

    @PropertyName("RollNumber")
    private String rollNumber;

    @PropertyName("Route_Name")
    private String routeName;

    @PropertyName("Student_Name")
    private String studentName;

    @PropertyName("schoolId")
    private String schoolId;

    @PropertyName("id")
    private String id;

    // Getters and Setters
    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdmissionName() {
        return admissionName;
    }

    public void setAdmissionName(String admissionName) {
        this.admissionName = admissionName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public String toString() {
        return "StudentDetails [aadhaarNumber=" + aadhaarNumber + ", address=" + address + ", admissionName="
                + admissionName + ", age=" + age + ", studentClass=" + studentClass + ", dob=" + dob + ", email="
                + email + ", fatherName=" + fatherName + ", fatherOccupation=" + fatherOccupation + ", gender=" + gender
                + ", motherName=" + motherName + ", motherOccupation=" + motherOccupation + ", phoneNumber="
                + phoneNumber + ", rollNumber=" + rollNumber + ", routeName=" + routeName
                + ", studentName=" + studentName + ", schoolId=" + schoolId + ", id=" + id + "]";
    }
}
