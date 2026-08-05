package com.smartnotification.service;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.smartnotification.client.AIClient;
import com.smartnotification.dto.EmailDto;
import com.smartnotification.dto.GenerateRequest;
import com.smartnotification.dto.GenerateResponse;
import com.smartnotification.entity.User;
import com.smartnotification.repository.ConnectedAccountRepository;
@Service
public class GmailService {

    private static final Logger log = LoggerFactory.getLogger(GmailService.class);
    private static final String APP_NAME = "Smart Notifications";
    private final PreferenceService preferenceService;
    private final ConnectedAccountRepository connectedAccountRepository;

    private final AIClient aiClient;
    private final GoogleCredentialService googleCredentialService;
    private final OAuthTokenService oauthTokenService;

    public GmailService(
            AIClient aiClient,
            GoogleCredentialService googleCredentialService,
            OAuthTokenService oauthTokenService,
            PreferenceService preferenceService,
            ConnectedAccountRepository connectedAccountRepository) {

        this.aiClient = aiClient;
        this.googleCredentialService = googleCredentialService;
        this.oauthTokenService = oauthTokenService;
        this.preferenceService = preferenceService;
        this.connectedAccountRepository = connectedAccountRepository;
    }
    /**
     * Builds a Gmail API client authenticated with the given access token.
     */
    private Gmail buildGmailClient(String accessToken)
            throws GeneralSecurityException, IOException {

        GoogleCredentials credentials =
                GoogleCredentials.create(new AccessToken(accessToken, null));

        HttpRequestInitializer requestInitializer =
                new HttpCredentialsAdapter(credentials);

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer
        )
        .setApplicationName(APP_NAME)
        .build();
    }

    /**
     * Retrieves a header value (e.g. "From", "Subject") from a Gmail message.
     */
    private String getHeader(Message message, String headerName) {

        for (var header : message.getPayload().getHeaders()) {
            if (headerName.equalsIgnoreCase(header.getName())) {
                return header.getValue();
            }
        }

        return "";
    }

    public EmailDto getLatestEmail(String accessToken, String email) {

        try {

            Gmail gmail = buildGmailClient(accessToken);

            var response = gmail.users()
                    .messages()
                    .list("me")
                    .setMaxResults(1L)
                    .execute();


            if (response.getMessages() == null ||
                    response.getMessages().isEmpty()) {

                return new EmailDto("", "", "No emails found.");
            }


            String messageId =
                    response.getMessages()
                    .get(0)
                    .getId();


            Message message =
                    gmail.users()
                    .messages()
                    .get("me", messageId)
                    .setFormat("full")
                    .execute();


            return processMessage(message, email);


        } catch (Exception e) {

            log.error("Failed fetching latest email", e);

            return new EmailDto("", "", e.getMessage());
        }
    }

    private String extractBody(MessagePart part) {

        if (part == null) {
            return "";
        }

        // Plain text body
        if ("text/plain".equals(part.getMimeType())
                && part.getBody() != null
                && part.getBody().getData() != null) {

            byte[] decoded = Base64.getUrlDecoder()
                    .decode(part.getBody().getData());

            return new String(decoded, StandardCharsets.UTF_8);
        }

        // Search nested parts
        if (part.getParts() != null) {

            for (MessagePart child : part.getParts()) {

                String body = extractBody(child);

                if (!body.isBlank()) {
                    return body;
                }
            }
        }

        return "";
    }

    public WatchResponse startWatch(String accessToken) {

        try {

            Gmail gmail = buildGmailClient(accessToken);

            WatchRequest watchRequest = new WatchRequest();
            watchRequest.setTopicName(
                    "projects/smart-notification-poc/topics/gmail-notifications"
            );

            return gmail.users()
                    .watch("me", watchRequest)
                    .execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getCurrentHistoryId(String accessToken) {

        try {

            Gmail gmail = buildGmailClient(accessToken);

            return gmail.users()
                    .getProfile("me")
                    .execute()
                    .getHistoryId();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public java.util.List<EmailDto> getHistory(String email, Long historyId)
            throws Exception {

        String accessToken =
                googleCredentialService.getValidAccessToken(email);

        Gmail gmail = buildGmailClient(accessToken);


        ListHistoryResponse historyResponse =
                gmail.users()
                .history()
                .list("me")
                .setStartHistoryId(
                        BigInteger.valueOf(historyId)
                )
                .setHistoryTypes(
                        java.util.List.of("messageAdded")
                )
                .execute();



        if (historyResponse.getHistory() == null) {

            // Still update to latest Gmail history
            if (historyResponse.getHistoryId() != null) {
                oauthTokenService.updateHistoryId(
                        email,
                        historyResponse.getHistoryId().longValue()
                );
            }

            return new java.util.ArrayList<>();
        }



        java.util.List<EmailDto> emails = new java.util.ArrayList<>();

        long latestHistoryId = historyId;


        try {

            for (var history : historyResponse.getHistory()) {


                if (history.getId() != null &&
                        history.getId().longValue() > latestHistoryId) {

                    latestHistoryId =
                            history.getId().longValue();
                }

                System.out.println("\n========== GMAIL HISTORY ==========");
                System.out.println("History ID     : " + history.getId());
                System.out.println("Messages Added : " +
                        (history.getMessagesAdded() == null ? 0 : history.getMessagesAdded().size()));
                System.out.println("===================================\n");

                if (history.getMessagesAdded() == null) {
                    continue;
                }


                for (var added : history.getMessagesAdded()) {

                    System.out.println("Processing Message ID : " + added.getMessage().getId());

                    Message message;
                    try {
                        message = getMessage(
                                accessToken,
                                added.getMessage().getId()
                        );
                    } catch (Exception e) {
                        log.error("getMessage failed for messageId={}, skipping. reason={}",
                                added.getMessage().getId(), e.getMessage(), e);
                        continue;
                    }

                    emails.add(
                            processMessage(
                                    message,
                                    email
                            )
                    );
                }
            }

        } finally {

            try {

                if (latestHistoryId > historyId) {

                    System.out.println("\nUpdating lastHistoryId from "
                            + historyId + " to " + latestHistoryId);

                    oauthTokenService.updateHistoryId(
                            email,
                            latestHistoryId
                    );

                    System.out.println("lastHistoryId updated successfully.\n");
                }

            } catch (Exception e) {

                log.error("Failed to update lastHistoryId", e);
            }
        }


        return emails;
    }
    public Message getMessage(String accessToken, String messageId)
            throws IOException, GeneralSecurityException {

        Gmail gmail = buildGmailClient(accessToken);

        return gmail.users()
                .messages()
                .get("me", messageId)
                .setFormat("full")
                .execute();
    }

    private EmailDto processMessage(Message message, String gmailEmail) {

        String from = getHeader(message, "From");
        String subject = getHeader(message, "Subject");
        String body = extractBody(message.getPayload());

        log.debug("Processing message. From: {} | Subject: {}", from, subject);

        User user = connectedAccountRepository
                .findByGmailEmail(gmailEmail)
                .orElseThrow()
                .getUser();

        String prompt = preferenceService.getPromptForUser(user);

        GenerateRequest request = new GenerateRequest(
                from,
                subject,
                body,
                prompt
        );

        EmailDto email = new EmailDto(from, subject, body);
        email.setGmailMessageId(message.getId());

        try {

            GenerateResponse aiResponse = aiClient.analyzeEmail(request);

            email.setNotify(aiResponse.isNotify());
            email.setPriority(aiResponse.getPriority());
            email.setSummary(aiResponse.getSummary());
            email.setReason(aiResponse.getReason());

        } catch (Exception e) {

            log.error("AI analysis failed for message {}", message.getId(), e);

            email.setNotify(false);
            email.setPriority("LOW");
            email.setSummary("AI service unavailable");
            email.setReason("AI analysis failed");
        }

        return email;
    }
}