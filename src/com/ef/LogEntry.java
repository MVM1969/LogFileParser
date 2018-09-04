package com.ef;

/*
 * @author Mark Marlow
 * @version 1.0
 * @created 9/1/18
 * 
 * This class represents a row of data in the LOG_ENTRY table
 */

import java.util.Date;

public class LogEntry {
	private Date entryDate;
	private String ipAddress;
	private String request;
	private int status;
	private String userAgent;

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public String toString() {
		return "Entry Date:" + getEntryDate() + "    IP Address:" + getIpAddress() + "    Request:" + getRequest()
				+ "    Status:" + getStatus() + "    User Agent:" + getUserAgent();
	}

}
