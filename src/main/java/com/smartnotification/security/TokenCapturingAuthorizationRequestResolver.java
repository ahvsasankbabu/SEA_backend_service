package com.smartnotification.security;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.smartnotification.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

public class TokenCapturingAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver delegate;
    private final JwtService jwtService;

    public TokenCapturingAuthorizationRequestResolver(
            ClientRegistrationRepository repo, JwtService jwtService) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
        this.jwtService = jwtService;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        captureUser(request);
        return delegate.resolve(request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
        captureUser(request);
        return delegate.resolve(request, registrationId);
    }

    private void captureUser(HttpServletRequest request) {
        String token = request.getParameter("token");
        if (token == null) return;
        try {
            String email = jwtService.extractEmail(token);
            if (email != null) {
                request.getSession(true).setAttribute("APP_USER_EMAIL", email);
            }
        } catch (Exception ignored) {
            // bad/expired token — leave session attribute unset,
            // existing orElseThrow in the handler fails safely
        }
    }
}