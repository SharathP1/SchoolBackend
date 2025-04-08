package com.synectiks.school.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class DeleteMarksRequest {
    @PropertyName("studentId")
    private String studentId;

    @PropertyName("subject")
    private String subject;

    // Default constructor (required for Firestore deserialization)
    public DeleteMarksRequest() {}

    // Parameterized constructor
    public DeleteMarksRequest(String studentId, String subject) {
        this.studentId = studentId;
        this.subject = subject;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}