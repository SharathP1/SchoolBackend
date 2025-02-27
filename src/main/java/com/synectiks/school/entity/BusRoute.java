package com.synectiks.school.entity;

public class BusRoute {
	 private String busNo;
	    private String busTrackingURL;
	    private String driverName;
	    private String routeName;
	    private String id;
	    private String schoolId;

	   

	    // Getters and Setters
	    public String getBusNo() {
	        return busNo;
	    }

	    public void setBusNo(String busNo) {
	        this.busNo = busNo;
	    }

	    public String getBusTrackingURL() {
	        return busTrackingURL;
	    }

	    public void setBusTrackingURL(String busTrackingURL) {
	        this.busTrackingURL = busTrackingURL;
	    }

	    public String getDriverName() {
	        return driverName;
	    }

	    public void setDriverName(String driverName) {
	        this.driverName = driverName;
	    }

	    public String getRouteName() {
	        return routeName;
	    }

	    public void setRouteName(String routeName) {
	        this.routeName = routeName;
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
			return "BusRoute [busNo=" + busNo + ", busTrackingURL=" + busTrackingURL + ", driverName=" + driverName
					+ ", routeName=" + routeName + ", id=" + id + ", schoolId=" + schoolId + "]";
		}

	    
}