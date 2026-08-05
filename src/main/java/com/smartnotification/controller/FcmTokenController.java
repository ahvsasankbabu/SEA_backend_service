package com.smartnotification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartnotification.dto.FcmTokenRequest;
import com.smartnotification.service.FcmTokenService;
import com.smartnotification.service.UserService;

@RestController
@RequestMapping("/fcm")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;
    private final UserService userService;

    public FcmTokenController(
            FcmTokenService fcmTokenService,
            UserService userService) {

        this.fcmTokenService = fcmTokenService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerToken(
            @RequestBody FcmTokenRequest request) {

        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        var user = userService.findByEmail(email)
                .orElseThrow();

        fcmTokenService.saveToken(
                user,
                request.getToken(),
                request.getDeviceName(),
                request.getPlatform()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Void> removeToken(
            @PathVariable String token) {

        fcmTokenService.deactivateToken(token);

        return ResponseEntity.noContent().build();
    }
}