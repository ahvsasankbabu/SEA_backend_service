package com.smartnotification.service;

import java.util.List;

import com.smartnotification.dto.PreferenceRequest;
import com.smartnotification.dto.PreferenceResponse;
import com.smartnotification.entity.User;

public interface PreferenceService {

    List<PreferenceResponse> getPreferences();

    PreferenceResponse addPreference(PreferenceRequest request);

    PreferenceResponse updatePreference(Long id, PreferenceRequest request);

    void deletePreference(Long id);

    String getPromptForCurrentUser();

    String getPromptForUser(User user);
}