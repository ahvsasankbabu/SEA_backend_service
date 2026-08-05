package com.smartnotification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.smartnotification.dto.AuthResponse;
import com.smartnotification.dto.LoginRequest;
import com.smartnotification.dto.RegisterRequest;
import com.smartnotification.entity.User;
import com.smartnotification.service.JwtService;
import com.smartnotification.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setPrimaryEmail(request.getPrimaryEmail());
        user.setPassword(request.getPassword());

        userService.register(user);

        String token = jwtService.generateToken(user.getPrimaryEmail());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPrimaryEmail(),
                        request.getPassword()));

        String token = jwtService.generateToken(request.getPrimaryEmail());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}