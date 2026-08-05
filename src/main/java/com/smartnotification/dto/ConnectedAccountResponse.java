package com.smartnotification.dto;

import java.time.LocalDateTime;

public class ConnectedAccountResponse {

    private Long id;
    private String gmailEmail;
    private String displayName;
    private boolean active;
    private LocalDateTime connectedAt;

    public ConnectedAccountResponse() {
    }

    public ConnectedAccountResponse(Long id, String gmailEmail,
                                    String displayName,
                                    boolean active,
                                    LocalDateTime connectedAt) {
        this.id = id;
        this.gmailEmail = gmailEmail;
        this.displayName = displayName;
        this.active = active;
        this.connectedAt = connectedAt;
    }

    public Long getId() {
        return id;
    }

    public String getGmailEmail() {
        return gmailEmail;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }
}