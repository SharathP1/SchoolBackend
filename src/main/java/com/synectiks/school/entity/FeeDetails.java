package com.synectiks.school.entity;

import java.util.Date;

public class FeeDetails {
	private String feeType;
    private String amt;
    private String amtPaid;
    private String status;
    private String dueDate;
    private String paidDate;

    public FeeDetails() {
		super();
	}

	// Constructor
    public FeeDetails(String feeType, String amt, String amtPaid, String status, String dueDate, String paidDate) {
        this.feeType = feeType;
        this.amt = amt;
        this.amtPaid = amtPaid;
        this.status = status;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
    }

    // Getters and Setters
    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getAmtPaid() {
        return amtPaid;
    }

    public void setAmtPaid(String d) {
        this.amtPaid = d;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String string) {
        this.dueDate = string;
    }

    public String getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(String paidDate) {
        this.paidDate = paidDate;
    }

    @Override
    public String toString() {
        return "FeeDetails{" +
                "feeType='" + feeType + '\'' +
                ", amt=" + amt +
                ", amt_paid=" + amtPaid +
                ", status='" + status + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", paidDate='" + paidDate + '\'' +
                '}';
    }
}
