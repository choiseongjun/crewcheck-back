package com.jun.crewcheckback.notification.dto;

import com.jun.crewcheckback.notification.domain.NotificationSetting;
import java.time.LocalTime;

public record NotificationSettingResponse(
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

    public NotificationSettingResponse(NotificationSetting setting) {
        this(
                setting.isCheckInReminder(),
                setting.isTeamActivityNotification(),
                setting.isStreakNotification(),
                setting.isNewMemberNotification(),
                setting.isAchievementNotification(),
                setting.getDoNotDisturbStart(),
                setting.getDoNotDisturbEnd(),
                setting.isReminder09(),
                setting.isReminder12(),
                setting.isReminder18(),
                setting.isReminder21());
    }
}
