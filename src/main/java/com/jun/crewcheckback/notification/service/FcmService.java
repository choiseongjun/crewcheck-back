package com.jun.crewcheckback.notification.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public String sendMessage(String token, String title, String body) {
        return sendMessage(token, title, body, null);
    }

    public String sendMessage(String token, String title, String body, Map<String, String> data) {
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
            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message: {}", e.getMessage());
            throw new RuntimeException("푸시 메시지 전송에 실패했습니다.", e);
        }
    }

    public BatchResponse sendMessages(List<String> tokens, String title, String body) {
        return sendMessages(tokens, title, body, null);
    }

    public BatchResponse sendMessages(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("No tokens to send messages");
            return null;
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
            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM multicast: {}", e.getMessage());
            throw new RuntimeException("푸시 메시지 전송에 실패했습니다.", e);
        }
    }

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
            throw new RuntimeException("토픽 메시지 전송에 실패했습니다.", e);
        }
    }

    public void subscribeToTopic(List<String> tokens, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(tokens, topic);
            log.info("Subscribed to topic: {} success, {} failure",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe to topic: {}", e.getMessage());
            throw new RuntimeException("토픽 구독에 실패했습니다.", e);
        }
    }

    public void unsubscribeFromTopic(List<String> tokens, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.unsubscribeFromTopic(tokens, topic);
            log.info("Unsubscribed from topic: {} success, {} failure",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe from topic: {}", e.getMessage());
            throw new RuntimeException("토픽 구독 해제에 실패했습니다.", e);
        }
    }
}
