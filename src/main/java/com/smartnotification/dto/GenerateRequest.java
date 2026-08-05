package com.smartnotification.dto;

public class GenerateRequest {
	
	private String emailSender;
	private String emailSubject;
	private String emailBody;
	private String userPreference;
	public String getEmailSender() {
		return emailSender;
	}
	public String getEmailSubject() {
		return emailSubject;
	}
	public String getEmailBody() {
		return emailBody;
	}
	public String getUserPreference() {
		return userPreference;
	}
	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}
	public void setUserPreference(String userPreference) {
		this.userPreference = userPreference;
	}
	public GenerateRequest() {
		
	}
	public GenerateRequest(String emailSender, String emailSubject, String emailBody, String userPreference) {
		super();
		this.emailSender = emailSender;
		this.emailSubject = emailSubject;
		this.emailBody = emailBody;
		this.userPreference = userPreference;
	}

}
