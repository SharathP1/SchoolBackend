package com.synectiks.school.entity;

import java.util.List;

import com.google.cloud.firestore.annotation.PropertyName;

public class Term {
    private String schoolId;
    private String termId;

    @PropertyName("name")
    private String termName;

    private String startDate;
    private String endDate;

    private List<Assessment> assessments;  // List of assessments with test IDs

    // Getters and Setters
    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    @PropertyName("name")
    public String getTermName() {
        return termName;
    }

    @PropertyName("name")
    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }
}
