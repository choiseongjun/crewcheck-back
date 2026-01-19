package com.jun.crewcheckback.notification.service;

import com.jun.crewcheckback.notification.domain.*;
import com.jun.crewcheckback.notification.dto.*;
import com.jun.crewcheckback.notification.repository.DeviceTokenRepository;
import com.jun.crewcheckback.notification.repository.NotificationRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final FcmService fcmService;
    private final NotificationRepository notificationRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerToken(String email, DeviceTokenRegisterRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        deviceTokenRepository.findByUserIdAndDeviceId(user.getId(), request.deviceId())
                .ifPresentOrElse(
                        token -> token.updateToken(request.fcmToken()),
                        () -> {
                            DeviceToken newToken = DeviceToken.builder()
                                    .user(user)
                                    .fcmToken(request.fcmToken())
                                    .deviceType(request.deviceType())
                                    .deviceId(request.deviceId())
                                    .build();
                            deviceTokenRepository.save(newToken);
                        }
                );
    }

    @Transactional
    public void removeToken(String email, String deviceId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        deviceTokenRepository.findByUserIdAndDeviceId(user.getId(), deviceId)
                .ifPresent(DeviceToken::deactivate);
    }

    @Transactional
    public void sendToUser(UUID userId, String title, String body, NotificationType type, UUID referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .notificationType(type)
                .referenceId(referenceId)
                .build();

        notificationRepository.save(notification);

        List<DeviceToken> tokens = deviceTokenRepository.findActiveTokensByUserId(userId);
        if (!tokens.isEmpty()) {
            List<String> fcmTokens = tokens.stream()
                    .map(DeviceToken::getFcmToken)
                    .toList();

            Map<String, String> data = Map.of(
                    "type", type.name(),
                    "referenceId", referenceId != null ? referenceId.toString() : ""
            );

            fcmService.sendMessages(fcmTokens, title, body, data);
            notification.markAsSent();
        }
    }

    @Transactional
    public void sendToUsers(List<UUID> userIds, String title, String body, NotificationType type, UUID referenceId) {
        for (UUID userId : userIds) {
            sendToUser(userId, title, body, type, referenceId);
        }
    }

    public List<NotificationResponse> getNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public List<NotificationResponse> getUnreadNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return notificationRepository.findUnreadByUserId(user.getId())
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public Long getUnreadCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return notificationRepository.countUnreadByUserId(user.getId());
    }

    @Transactional
    public void markAsRead(UUID notificationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("권한이 없습니다.");
        }

        notification.markAsRead();
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        notificationRepository.markAllAsReadByUserId(user.getId());
    }
}
