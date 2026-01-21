package com.jun.crewcheckback.user.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private String bio;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name = "social_id")
    private String socialId;

    @Builder
    public User(String email, String password, String nickname, String profileImageUrl, String bio,
            LocalDate birthDate, Gender gender, AuthProvider authProvider, String socialId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.birthDate = birthDate;
        this.gender = gender;
        this.authProvider = authProvider;
        this.socialId = socialId;
    }

    public void update(String nickname, String bio, String profileImageUrl, LocalDate birthDate, Gender gender) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (bio != null) {
            this.bio = bio;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (gender != null) {
            this.gender = gender;
        }
    }

    @Column(name = "device_token")
    private String deviceToken;

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
