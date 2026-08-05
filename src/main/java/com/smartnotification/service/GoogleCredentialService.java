package com.smartnotification.service;



import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.auth.oauth2.TokenResponse;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.AccessToken;
import com.smartnotification.entity.OAuthToken;

@Service
public class GoogleCredentialService {
	
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;

    private final OAuthTokenService oauthTokenService;

    public GoogleCredentialService(OAuthTokenService oauthTokenService) {
        this.oauthTokenService = oauthTokenService;
    }

    public String getValidAccessToken(String email) throws Exception {

        OAuthToken token = oauthTokenService
                .getToken(email)
                .orElseThrow();

        if (token.getExpiresAt() != null &&
                token.getExpiresAt().isAfter(Instant.now().plusSeconds(60))) {

            return token.getAccessToken();
        }

        TokenResponse response = new GoogleRefreshTokenRequest(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                token.getRefreshToken(),
                clientId,
                clientSecret
        ).execute();

        token.setAccessToken(response.getAccessToken());

        if (response.getExpiresInSeconds() != null) {
            token.setExpiresAt(
                    Instant.now().plusSeconds(response.getExpiresInSeconds())
            );
        }

        oauthTokenService.save(token);

        return response.getAccessToken();
    }
}
