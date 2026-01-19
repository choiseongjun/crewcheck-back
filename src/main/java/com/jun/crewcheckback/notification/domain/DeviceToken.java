package com.jun.crewcheckback.notification.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import com.jun.crewcheckback.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "device_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false, length = 500)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 20)
    private DeviceType deviceType;

    @Column(name = "device_id", length = 200)
    private String deviceId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public DeviceToken(User user, String fcmToken, DeviceType deviceType, String deviceId) {
        this.user = user;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.isActive = true;
    }

    public void updateToken(String fcmToken) {
        this.fcmToken = fcmToken;
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
