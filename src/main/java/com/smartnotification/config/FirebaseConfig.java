package com.smartnotification.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CREDENTIALS_JSON}")
    private String firebaseCredentialsJson;

    @PostConstruct
    public void initialize() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        GoogleCredentials.fromStream(
                                new ByteArrayInputStream(
                                        firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8))))
                .build();

        FirebaseApp.initializeApp(options);
    }
}