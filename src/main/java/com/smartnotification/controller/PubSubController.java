package com.smartnotification.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.smartnotification.dto.EmailDto;
import com.smartnotification.entity.ConnectedAccount;
import com.smartnotification.entity.FcmToken;
import com.smartnotification.entity.OAuthToken;
import com.smartnotification.entity.User;
import com.smartnotification.repository.ConnectedAccountRepository;
import com.smartnotification.service.FcmTokenService;
import com.smartnotification.service.FirebaseNotificationService;
import com.smartnotification.service.GmailService;
import com.smartnotification.service.NotificationService;
import com.smartnotification.service.OAuthTokenService;

import tools.jackson.databind.ObjectMapper;

@RestController
public class PubSubController {

    private final GmailService gmailService;
    private final OAuthTokenService oauthTokenService;
    private final FirebaseNotificationService firebaseNotificationService;
    private final ConnectedAccountRepository connectedAccountRepository;
    private final FcmTokenService fcmTokenService;
    private final NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PubSubController(
            GmailService gmailService,
            OAuthTokenService oauthTokenService,
            FirebaseNotificationService firebaseNotificationService,
            ConnectedAccountRepository connectedAccountRepository,
            FcmTokenService fcmTokenService,
            NotificationService notificationService) {

        this.gmailService = gmailService;
        this.oauthTokenService = oauthTokenService;
        this.firebaseNotificationService = firebaseNotificationService;
        this.connectedAccountRepository = connectedAccountRepository;
        this.fcmTokenService = fcmTokenService;
        this.notificationService = notificationService;
    }

    @PostMapping("/gmail/push")
    public ResponseEntity<String> receiveNotification(
            @RequestBody(required = false) String body)
            throws Exception {

        System.out.println("========== PUB/SUB RECEIVED ==========");
        System.out.println(body);

        var root = objectMapper.readTree(body);

        var encodedData =
                root.get("message")
                    .get("data")
                    .asText();

        String decodedJson =
                new String(
                        Base64.getDecoder().decode(encodedData),
                        StandardCharsets.UTF_8);

        System.out.println("Decoded JSON:");
        System.out.println(decodedJson);

        var gmailData = objectMapper.readTree(decodedJson);

        String email =
                gmailData.get("emailAddress").asText();

        Long historyId =
                gmailData.get("historyId").asLong();

        System.out.println("Email: " + email);
        System.out.println("History ID: " + historyId);

        var tokenOptional =
                oauthTokenService.getToken(email);

        if (tokenOptional.isEmpty()) {

            System.out.println("No OAuth token found");

            return ResponseEntity.ok("Ignored");
        }

        OAuthToken token = tokenOptional.get();

        System.out.println(
                "DB lastHistoryId = "
                        + token.getLastHistoryId());

        if (historyId != null && token.getLastHistoryId() != null
                && historyId <= token.getLastHistoryId()) {

            System.out.println("Duplicate/stale Pub/Sub notification, skipping. incoming="
                    + historyId + " lastProcessed=" + token.getLastHistoryId());

            return ResponseEntity.ok("Already processed");
        }

        List<EmailDto> emailDtos =
                gmailService.getHistory(
                        token.getEmail(),
                        token.getLastHistoryId());

        System.out.println("========== EMAIL DTOS ==========");
        System.out.println(emailDtos);

        ConnectedAccount connectedAccount =
                connectedAccountRepository
                        .findByGmailEmail(email)
                        .orElseThrow();

        User user = connectedAccount.getUser();

        for (EmailDto emailDto : emailDtos) {

            System.out.println(
                    "AI NOTIFY = "
                            + emailDto.isNotify());

            System.out.println(
                    "AI SUMMARY = "
                            + emailDto.getSummary());

            System.out.println(
                    "AI PRIORITY = "
                            + emailDto.getPriority());

            notificationService.saveNotification(emailDto, user, connectedAccount);

            if (emailDto.isNotify()) {

                for (FcmToken fcmToken :
                        fcmTokenService.getActiveTokens(user)) {

                    System.out.println(
                            "Sending AI result to token: "
                                    + fcmToken.getToken());

                    firebaseNotificationService.sendNotification(
                            fcmToken.getToken(),
                            emailDto.getFrom(),
                            emailDto.getSubject(),
                            emailDto.getSummary(),
                            emailDto.getReason(),
                            emailDto.getPriority(),
                            emailDto.isNotify(),
                            email);
                }
            }
        }

        System.out.println("======================================");

        return ResponseEntity.ok("Received");
    }
}