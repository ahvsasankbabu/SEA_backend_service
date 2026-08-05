package com.smartnotification.controller;

import java.util.List;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.gmail.model.WatchResponse;
import com.smartnotification.entity.ConnectedAccount;
import com.smartnotification.entity.User;
import com.smartnotification.repository.ConnectedAccountRepository;
import com.smartnotification.repository.UserRepository;
import com.smartnotification.service.FirebaseNotificationService;
import com.smartnotification.service.GmailService;
import com.smartnotification.service.JwtService;
import com.smartnotification.service.OAuthTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
public class GmailController {


    private final FirebaseNotificationService firebaseNotificationService;
    private final GmailService gmailService;
    private final OAuthTokenService oauthTokenService;

    private final ConnectedAccountRepository connectedAccountRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;



    public GmailController(
            GmailService gmailService,
            OAuthTokenService oauthTokenService,
            FirebaseNotificationService firebaseNotificationService,
            ConnectedAccountRepository connectedAccountRepository,
            UserRepository userRepository,
            JwtService jwtService) {


        this.gmailService = gmailService;
        this.oauthTokenService = oauthTokenService;
        this.firebaseNotificationService = firebaseNotificationService;

        this.connectedAccountRepository =
                connectedAccountRepository;

        this.userRepository =
                userRepository;

        this.jwtService =
                jwtService;
    }



    @GetMapping("/gmail")
    public void gmail(
            @RegisteredOAuth2AuthorizedClient("google")
            OAuth2AuthorizedClient authorizedClient,

            OAuth2AuthenticationToken authenticationToken,

            HttpServletRequest request,

            HttpServletResponse response)
            throws Exception {



        String accessToken =
                authorizedClient.getAccessToken()
                        .getTokenValue();



        String refreshToken = null;


        if (authorizedClient.getRefreshToken() != null) {

            refreshToken =
                    authorizedClient.getRefreshToken()
                            .getTokenValue();
        }



        // Gmail account being connected

        OAuth2User googleUser =
                authenticationToken.getPrincipal();



        String gmailEmail =
                googleUser.getAttribute("email");



        // Primary Smart Notification user

        String token =
                request.getParameter("token");



        if (token == null || token.isBlank()) {

            throw new RuntimeException(
                    "Missing JWT token"
            );
        }



        String primaryEmail =
                jwtService.extractEmail(token);



        User appUser =
                userRepository
                .findByPrimaryEmail(primaryEmail)
                .orElseThrow();



        // Save Gmail OAuth token

        oauthTokenService.saveToken(
                gmailEmail,
                accessToken,
                refreshToken,
                authorizedClient.getAccessToken()
                        .getExpiresAt()
        );



        // Create connected account mapping

        if (connectedAccountRepository
                .existsByGmailEmail(gmailEmail)) {


            ConnectedAccount existing =
                    connectedAccountRepository
                    .findByGmailEmail(gmailEmail)
                    .orElseThrow();



            if (!existing.getUser()
                    .getId()
                    .equals(appUser.getId())) {


                throw new RuntimeException(
                    "Gmail account already connected to another user"
                );
            }


        } else {


            ConnectedAccount account =
                    new ConnectedAccount();


            account.setUser(appUser);

            account.setGmailEmail(gmailEmail);

            account.setDisplayName(gmailEmail);

            account.setActive(true);


            connectedAccountRepository.save(account);
        }



        // Start Gmail watch

        WatchResponse watchResponse =
                gmailService.startWatch(accessToken);



        oauthTokenService.updateHistoryId(
                gmailEmail,
                watchResponse.getHistoryId()
                        .longValue()
        );



        response.sendRedirect(
                "smartnotify://oauth-success"
        );
    }



    @GetMapping("/test-notification")
    public String testNotification() {


        try {


        	firebaseNotificationService.sendNotification(
        	        "YOUR_FCM_TOKEN",
        	        "Test Sender",
        	        "Smart Notification",
        	        "Push notifications are working!",
        	        "This is a test notification.",
        	        "HIGH",
        	        true,
        	        "test@gmail.com"
        	);


            return "Notification sent successfully.";


        } catch (Exception e) {


            e.printStackTrace();

            return "Exception: "
                    + e.getMessage();
        }
    }
}