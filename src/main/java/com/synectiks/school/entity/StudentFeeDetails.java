package com.synectiks.school.entity;

public class StudentFeeDetails {
    private String amt;
    private String amtPaid;
    private String dueDate;
    private String feeType;
    private String paidDate;
    private String status;

    // Add these if not using Lombok
    public String getAmt() { return amt; }
    public void setAmt(String amt) { this.amt = amt; }

    public String getAmtPaid() { return amtPaid; }
    public void setAmtPaid(String amtPaid) { this.amtPaid = amtPaid; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public String getPaidDate() { return paidDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
