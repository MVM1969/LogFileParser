package com.ef;

/*
 * @author Mark Marlow
 * @version 1.0
 * @created 9/1/18
 * 
 * This class represents a row of data in the BLOCKED_REQUESTS table
 */

import java.util.Date;

public class BlockedRequest {

	private Date startDate;
	private Date endDate;
	private String ipAddress;
	private String duration;
	private int requests;
	private String reason;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int getRequests() {
		return requests;
	}

	public void setRequests(int requests) {
		this.requests = requests;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "Start Date:" + getStartDate() + "   End Date:" + getEndDate() + "    IP Address:" + getIpAddress()
				+ "    Duration:" + getDuration() + "   Requests:" + getRequests() + "     Reason:" + getReason();
	}

}
