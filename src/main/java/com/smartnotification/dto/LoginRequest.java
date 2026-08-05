package com.smartnotification.dto;

public class LoginRequest {

    private String primaryEmail;
    private String password;

    public LoginRequest() {
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}