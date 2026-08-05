package com.smartnotification.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.smartnotification.entity.User;
import com.smartnotification.repository.UserRepository;
import com.smartnotification.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {


    private final UserRepository userRepository;
    private final JwtService jwtService;


    public OAuth2SuccessHandler(
            UserRepository userRepository,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }



    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {


        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();



        String gmailEmail =
                oauthUser.getAttribute("email");



        User user =
                userRepository
                .findByPrimaryEmail(gmailEmail)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Primary user not found"
                    )
                );



        String token =
                jwtService.generateToken(
                    user.getPrimaryEmail()
                );



        response.sendRedirect(
            "/gmail?token=" + token
        );
    }
}