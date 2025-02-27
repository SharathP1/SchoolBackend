package com.synectiks.school.entity;

public class AttendanceRecord {
	  private String period;
	    private String time;
	    private boolean present;

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

		@Override
		public String toString() {
			return "AttendanceRecord [period=" + period + ", time=" + time + ", present=" + present + "]";
		}
	    
}
