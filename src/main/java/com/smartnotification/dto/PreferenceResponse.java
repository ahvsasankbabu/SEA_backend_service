package com.smartnotification.dto;

public class PreferenceResponse {

    private Long id;
    private String prompt;
    private boolean active;

    public PreferenceResponse() {
    }

    public PreferenceResponse(Long id, String prompt, boolean active) {
        this.id = id;
        this.prompt = prompt;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}