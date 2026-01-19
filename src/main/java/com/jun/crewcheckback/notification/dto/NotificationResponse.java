package com.jun.crewcheckback.notification.dto;

import com.jun.crewcheckback.notification.domain.Notification;
import com.jun.crewcheckback.notification.domain.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String body,
        NotificationType notificationType,
        UUID referenceId,
        Boolean isRead,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getNotificationType(),
                notification.getReferenceId(),
                notification.getIsRead(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}
