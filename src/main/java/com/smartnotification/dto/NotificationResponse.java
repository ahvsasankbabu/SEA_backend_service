package com.smartnotification.dto;

import java.time.LocalDateTime;

import com.smartnotification.entity.Notification;

public class NotificationResponse {

    private Long id;
    private String sender;
    private String subject;
    private String summary;
    private String reason;
    private String body;
    private String priority;
    private boolean notify;
    private boolean read;
    private LocalDateTime receivedAt;
    private String connectedAccountEmail;

    public static NotificationResponse from(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.id = n.getId();
        r.sender = n.getSender();
        r.subject = n.getSubject();
        r.summary = n.getSummary();
        r.reason = n.getReason();
        r.body = n.getBody();
        r.priority = n.getPriority();
        r.notify = n.isNotify();
        r.read = n.isRead();
        r.receivedAt = n.getReceivedAt();
        r.connectedAccountEmail = n.getConnectedAccount().getGmailEmail();
        return r;
    }

    public Long getId() { return id; }
    public String getSender() { return sender; }
    public String getSubject() { return subject; }
    public String getSummary() { return summary; }
    public String getReason() { return reason; }
    public String getBody() { return body; }
    public String getPriority() { return priority; }
    public boolean isNotify() { return notify; }
    public boolean isRead() { return read; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public String getConnectedAccountEmail() { return connectedAccountEmail; }
}