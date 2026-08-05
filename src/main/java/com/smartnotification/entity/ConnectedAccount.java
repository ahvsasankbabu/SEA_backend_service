package com.smartnotification.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "connected_accounts")
public class ConnectedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "gmail_email", nullable = false, unique = true)
    private String gmailEmail;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "connected_at", nullable = false, updatable = false)
    private LocalDateTime connectedAt;

    public ConnectedAccount() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGmailEmail() {
        return gmailEmail;
    }

    public void setGmailEmail(String gmailEmail) {
        this.gmailEmail = gmailEmail;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }
}