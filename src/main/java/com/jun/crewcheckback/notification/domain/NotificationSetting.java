package com.jun.crewcheckback.notification.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import com.jun.crewcheckback.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_settings")
public class NotificationSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean checkInReminder;
    private boolean teamActivityNotification;
    private boolean streakNotification;
    private boolean newMemberNotification;
    private boolean achievementNotification;

    private LocalTime doNotDisturbStart;
    private LocalTime doNotDisturbEnd;

    private boolean reminder09;
    private boolean reminder12;
    private boolean reminder18;
    private boolean reminder21;

    @Builder
    public NotificationSetting(User user, boolean checkInReminder, boolean teamActivityNotification,
            boolean streakNotification, boolean newMemberNotification, boolean achievementNotification,
            LocalTime doNotDisturbStart, LocalTime doNotDisturbEnd, boolean reminder09, boolean reminder12,
            boolean reminder18, boolean reminder21) {
        this.user = user;
        this.checkInReminder = checkInReminder;
        this.teamActivityNotification = teamActivityNotification;
        this.streakNotification = streakNotification;
        this.newMemberNotification = newMemberNotification;
        this.achievementNotification = achievementNotification;
        this.doNotDisturbStart = doNotDisturbStart;
        this.doNotDisturbEnd = doNotDisturbEnd;
        this.reminder09 = reminder09;
        this.reminder12 = reminder12;
        this.reminder18 = reminder18;
        this.reminder21 = reminder21;
    }

    public void update(boolean checkInReminder, boolean teamActivityNotification, boolean streakNotification,
            boolean newMemberNotification, boolean achievementNotification, LocalTime doNotDisturbStart,
            LocalTime doNotDisturbEnd, boolean reminder09, boolean reminder12, boolean reminder18, boolean reminder21) {
        this.checkInReminder = checkInReminder;
        this.teamActivityNotification = teamActivityNotification;
        this.streakNotification = streakNotification;
        this.newMemberNotification = newMemberNotification;
        this.achievementNotification = achievementNotification;
        this.doNotDisturbStart = doNotDisturbStart;
        this.doNotDisturbEnd = doNotDisturbEnd;
        this.reminder09 = reminder09;
        this.reminder12 = reminder12;
        this.reminder18 = reminder18;
        this.reminder21 = reminder21;
    }

    public static NotificationSetting createDefault(User user) {
        return NotificationSetting.builder()
                .user(user)
                .checkInReminder(true)
                .teamActivityNotification(true)
                .streakNotification(true)
                .newMemberNotification(true)
                .achievementNotification(true)
                .doNotDisturbStart(null)
                .doNotDisturbEnd(null)
                .reminder09(false)
                .reminder12(true)
                .reminder18(false)
                .reminder21(true)
                .build();
    }
}
