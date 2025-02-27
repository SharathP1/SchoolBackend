package com.synectiks.school.entity;

public class FeeDetails {
	private String feeType;
    private double amt;
    private double amtPaid;
    private String status;
    private String dueDate;
    private String paidDate;

    public FeeDetails() {
		super();
	}

	// Constructor
    public FeeDetails(String feeType, double amt, double amtPaid, String status, String dueDate, String paidDate) {
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

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public double getAmtPaid() {
        return amtPaid;
    }

    public void setAmtPaid(double amtPaid) {
        this.amtPaid = amtPaid;
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

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
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
                ", amtPaid=" + amtPaid +
                ", status='" + status + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", paidDate='" + paidDate + '\'' +
                '}';
    }
}
