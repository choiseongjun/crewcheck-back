package com.jun.crewcheckback.notification.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    @Async
    public void sendMessage(String token, String title, String body) {
        sendMessage(token, title, body, null);
    }

    @Async
    public void sendMessage(String token, String title, String body, Map<String, String> data) {
        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (data != null && !data.isEmpty()) {
            messageBuilder.putAllData(data);
        }

        try {
            String response = firebaseMessaging.send(messageBuilder.build());
            log.info("FCM message sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message: {}", e.getMessage());
            // Async method exception handling - usually logs are enough for notifications
        }
    }

    @Async
    public void sendMessages(List<String> tokens, String title, String body) {
        sendMessages(tokens, title, body, null);
    }

    @Async
    public void sendMessages(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("No tokens to send messages");
            return;
        }

        MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (data != null && !data.isEmpty()) {
            messageBuilder.putAllData(data);
        }

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(messageBuilder.build());
            log.info("FCM multicast sent: {} success, {} failure",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM multicast: {}", e.getMessage());
        }
    }

    @Async
    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        Message.Builder messageBuilder = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (data != null && !data.isEmpty()) {
            messageBuilder.putAllData(data);
        }

        try {
            String response = firebaseMessaging.send(messageBuilder.build());
            log.info("FCM topic message sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM topic message: {}", e.getMessage());
        }
    }

    @Async
    public void subscribeToTopic(List<String> tokens, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(tokens, topic);
            log.info("Subscribed to topic: {} success, {} failure",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe to topic: {}", e.getMessage());
        }
    }

    @Async
    public void unsubscribeFromTopic(List<String> tokens, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.unsubscribeFromTopic(tokens, topic);
            log.info("Unsubscribed from topic: {} success, {} failure",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe from topic: {}", e.getMessage());
        }
    }
}
