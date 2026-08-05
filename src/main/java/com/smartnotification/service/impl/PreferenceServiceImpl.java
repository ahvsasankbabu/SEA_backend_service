package com.smartnotification.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.smartnotification.dto.PreferenceRequest;
import com.smartnotification.dto.PreferenceResponse;
import com.smartnotification.entity.Preference;
import com.smartnotification.entity.User;
import com.smartnotification.repository.PreferenceRepository;
import com.smartnotification.service.PreferenceService;
import com.smartnotification.service.UserService;


@Service
public class PreferenceServiceImpl implements PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserService userService;

    public PreferenceServiceImpl(
            PreferenceRepository preferenceRepository,
            UserService userService) {

        this.preferenceRepository = preferenceRepository;
        this.userService = userService;
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<PreferenceResponse> getPreferences() {

        User user = getCurrentUser();

        return preferenceRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PreferenceResponse addPreference(PreferenceRequest request) {

        User user = getCurrentUser();

        Preference preference = new Preference();
        preference.setUser(user);
        preference.setPrompt(request.getPrompt());
        preference.setActive(true);

        Preference saved = preferenceRepository.save(preference);

        return mapToResponse(saved);
    }

    @Override
    public PreferenceResponse updatePreference(
            Long id,
            PreferenceRequest request) {

        User user = getCurrentUser();

        Preference preference = preferenceRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Preference not found"));

        preference.setPrompt(request.getPrompt());

        Preference updated = preferenceRepository.save(preference);

        return mapToResponse(updated);
    }

    @Override
    public void deletePreference(Long id) {

        User user = getCurrentUser();

        Preference preference = preferenceRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Preference not found"));

        preferenceRepository.delete(preference);
    }

    @Override
    public String getPromptForCurrentUser() {
        return getPromptForUser(getCurrentUser());
    }

    @Override
    public String getPromptForUser(User user) {

        return preferenceRepository.findByUserAndActiveTrue(user)
                .stream()
                .map(Preference::getPrompt)
                .collect(Collectors.joining("\n"));
    }

    private PreferenceResponse mapToResponse(Preference preference) {

        PreferenceResponse response = new PreferenceResponse();

        response.setId(preference.getId());
        response.setPrompt(preference.getPrompt());
        response.setActive(preference.isActive());

        return response;
    }
}