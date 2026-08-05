package com.smartnotification.service;
import java.util.List;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smartnotification.entity.OAuthToken;
import com.smartnotification.repository.OAuthTokenRepository;

@Service
public class OAuthTokenService {

    private final OAuthTokenRepository repository;

    public OAuthTokenService(OAuthTokenRepository repository) {
        this.repository = repository;
    }

    public void saveToken(
            String email,
            String accessToken,
            String refreshToken,
            Instant expiresAt) {

        OAuthToken token = repository.findByEmail(email)
                .orElse(new OAuthToken());

        token.setEmail(email);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setExpiresAt(expiresAt);

        repository.save(token);
    }

    public Optional<OAuthToken> getToken(String email) {
        return repository.findByEmail(email);
    }
    public void updateHistoryId(String email, Long historyId) {

        OAuthToken token = repository.findByEmail(email)
                .orElseThrow();

        token.setLastHistoryId(historyId);

        repository.save(token);
    }
    public OAuthToken save(OAuthToken token) {
        return repository.save(token);
    }
    public List<String> getAllEmails() {

        return repository.findAll()
                .stream()
                .map(OAuthToken::getEmail)
                .toList();
    }
}