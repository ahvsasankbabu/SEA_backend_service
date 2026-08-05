package com.smartnotification.dto;

public class EmailDto {

    private String from;
    private String subject;
    private String body;
    private boolean notify;
    private String priority;
    private String summary;
    private String reason;
    private String gmailMessageId;

    public String getGmailMessageId() {
        return gmailMessageId;
    }

    public void setGmailMessageId(String gmailMessageId) {
        this.gmailMessageId = gmailMessageId;
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

	public EmailDto() {
    }

    public EmailDto(String from, String subject, String body) {
        this.from = from;
        this.subject = subject;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}