package com.smartnotification.dto;

public class GenerateResponse {

	
	private boolean notify;
	private String priority;
	private String summary;
	private String reason;
	public GenerateResponse() {
		super();
	}
	public GenerateResponse(boolean notify, String priority, String summary, String reason) {
		super();
		this.notify = notify;
		this.priority = priority;
		this.summary = summary;
		this.reason = reason;
	}
	public boolean isNotify() {
		return notify;
	}
	public String getPriority() {
		return priority;
	}
	public String getSummary() {
		return summary;
	}
	public String getReason() {
		return reason;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
}
