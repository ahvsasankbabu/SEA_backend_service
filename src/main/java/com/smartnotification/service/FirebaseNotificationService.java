package com.smartnotification.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;

@Service
public class FirebaseNotificationService {

    private final FcmTokenService fcmTokenService;

    public FirebaseNotificationService(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    public boolean sendNotification(
            String token,
            String sender,
            String subject,
            String summary,
            String reason,
            String priority,
            boolean notify,
            String receiverEmail) {

        Message message = Message.builder()
                .setToken(token)

                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .setNotification(
                                        AndroidNotification.builder()
                                                .setTitle(subject)
                                                .setBody(summary)
                                                .setChannelId("important_channel")
                                                .build()
                                )
                                .build()
                )

                .setNotification(
                        Notification.builder()
                                .setTitle(subject)
                                .setBody(summary)
                                .build()
                )

                // Complete payload for Flutter
                .putData("sender", sender)
                .putData("subject", subject)
                .putData("summary", summary)
                .putData("reason", reason)
                .putData("priority", priority)
                .putData("notify", String.valueOf(notify))
                .putData("receiverEmail", receiverEmail)

                .build();

        try {

            String response = FirebaseMessaging.getInstance().send(message);

            System.out.println("FCM Response = " + response);

            return true;

        } catch (FirebaseMessagingException e) {

            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {

                System.out.println("FCM token invalid, deactivating: " + token);

                fcmTokenService.deactivateToken(token);

            } else {

                System.out.println(
                        "FCM send failed: "
                                + e.getMessagingErrorCode()
                                + " - "
                                + e.getMessage());
            }

            return false;
        }
    }
}