package com.smartnotification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartnotification.dto.PreferenceRequest;
import com.smartnotification.dto.PreferenceResponse;
import com.smartnotification.service.PreferenceService;

@RestController
@RequestMapping("/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping
    public ResponseEntity<List<PreferenceResponse>> getPreferences() {
        return ResponseEntity.ok(preferenceService.getPreferences());
    }

    @PostMapping
    public ResponseEntity<PreferenceResponse> addPreference(
            @RequestBody PreferenceRequest request) {

        PreferenceResponse response =
                preferenceService.addPreference(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenceResponse> updatePreference(
            @PathVariable Long id,
            @RequestBody PreferenceRequest request) {

        return ResponseEntity.ok(
                preferenceService.updatePreference(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(
            @PathVariable Long id) {

        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }
}