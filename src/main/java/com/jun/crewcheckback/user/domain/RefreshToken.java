package com.jun.crewcheckback.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Builder
    public RefreshToken(String token, User user, String ipAddress, String userAgent,
                        String deviceInfo, LocalDateTime expiresAt) {
        this.token = token;
        this.user = user;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.deviceInfo = deviceInfo;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.lastUsedAt = LocalDateTime.now();
    }

    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
