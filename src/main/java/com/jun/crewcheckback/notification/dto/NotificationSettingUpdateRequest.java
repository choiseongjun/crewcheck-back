package com.jun.crewcheckback.notification.dto;

import java.time.LocalTime;

public record NotificationSettingUpdateRequest(
        boolean checkInReminder,
        boolean teamActivityNotification,
        boolean streakNotification,
        boolean newMemberNotification,
        boolean achievementNotification,
        LocalTime doNotDisturbStart,
        LocalTime doNotDisturbEnd,
        boolean reminder09,
        boolean reminder12,
        boolean reminder18,
        boolean reminder21) {
}
