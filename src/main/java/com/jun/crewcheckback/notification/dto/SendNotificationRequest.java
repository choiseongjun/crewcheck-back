package com.jun.crewcheckback.notification.dto;

import com.jun.crewcheckback.notification.domain.NotificationType;

import java.util.List;
import java.util.UUID;

public record SendNotificationRequest(
        List<UUID> userIds,
        String title,
        String body,
        NotificationType notificationType,
        UUID referenceId
) {}
