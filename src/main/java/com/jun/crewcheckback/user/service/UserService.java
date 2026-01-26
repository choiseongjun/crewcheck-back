package com.jun.crewcheckback.user.service;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.global.security.JwtTokenProvider;
import com.jun.crewcheckback.user.domain.RefreshToken;
import com.jun.crewcheckback.user.dto.*;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.RefreshTokenRepository;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final com.jun.crewcheckback.team.repository.TeamMemberRepository teamMemberRepository;
    private final com.jun.crewcheckback.checkin.repository.CheckInRepository checkInRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenValidityInMilliseconds;

    public UserStatsResponse getUserStats(java.util.UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int teamCount = teamMemberRepository.findAllByUser(user).size();

        List<CheckIn> checkIns = checkInRepository
                .findAllByUserAndStatusAndDeletedYn(user, "approved", "N");

        int totalCheckInCount = checkIns.size();
        int currentStreak = calculateStreak(checkIns);

        return UserStatsResponse.of(teamCount, totalCheckInCount, currentStreak);
    }

    private int calculateStreak(List<CheckIn> checkIns) {
        if (checkIns.isEmpty())
            return 0;

        List<LocalDate> dates = checkIns.stream()
                .map(c -> c.getTimestamp().toLocalDate())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (dates.isEmpty())
            return 0;

        int streak = 0;
        LocalDate current = LocalDate.now();

        // Check if the latest check-in is today or yesterday
        if (!dates.contains(current)) {
            if (dates.contains(current.minusDays(1))) {
                current = current.minusDays(1);
            } else {
                return 0; // Streak broken
            }
        }

        for (LocalDate date : dates) {
            if (date.equals(current)) {
                streak++;
                current = current.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    @Transactional
    public UserResponse registerUser(UserSignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .authProvider(com.jun.crewcheckback.user.domain.AuthProvider.EMAIL)
                .build();

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }

    @Transactional
    public TokenResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        if (request.getDeviceToken() != null && !request.getDeviceToken().isEmpty()) {
            user.updateDeviceToken(request.getDeviceToken());
        }

        String accessToken = jwtTokenProvider.createToken(user.getEmail());
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getEmail());

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshTokenValidityInMilliseconds / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceInfo(parseDeviceInfo(userAgent))
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional
    public TokenResponse socialLogin(SocialLoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .map(existingUser -> {
                    if ("Y".equals(existingUser.getDeletedYn())) {
                        throw new IllegalArgumentException(
                                "현재 아이디는 탈퇴하였습니다. 재가입하려면 crewcheck.inapp@gmail.com로 문의해주세요.");
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    // Create new user if not exists
                    return userRepository.save(User.builder()
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // Random password
                            .nickname(request.getNickname())
                            .profileImageUrl(request.getProfileImageUrl())
                            .authProvider(request.getProvider())
                            .socialId(request.getSocialId())
                            .build());
                });

        // Update device token if provided
        if (request.getDeviceToken() != null && !request.getDeviceToken().isEmpty()) {
            user.updateDeviceToken(request.getDeviceToken());
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.createToken(user.getEmail());
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getEmail());

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshTokenValidityInMilliseconds / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceInfo(parseDeviceInfo(userAgent))
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
            refreshTokenRepository.delete(token);
        }
    }

    @Transactional
    public void logoutAll(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        refreshTokenRepository.deleteAllByUser(user);
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new IllegalArgumentException("Refresh token expired");
        }

        User user = storedToken.getUser();

        // Rotate Refresh Token
        refreshTokenRepository.delete(storedToken);

        String newAccessToken = jwtTokenProvider.createToken(user.getEmail());
        String newRefreshTokenValue = jwtTokenProvider.createRefreshToken(user.getEmail());

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshTokenValidityInMilliseconds / 1000);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenValue)
                .user(user)
                .ipAddress(storedToken.getIpAddress())
                .userAgent(storedToken.getUserAgent())
                .deviceInfo(storedToken.getDeviceInfo())
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .tokenType("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    public UserResponse getUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserResponse(user);
    }

    public List<UserRankingResponse> getUserRanking() {
        List<Object[]> results = userRepository
                .findUserAchievementRates(PageRequest.of(0, 10));

        return results.stream()
                .map(result -> {
                    User user = (User) result[0];
                    int rate = ((Number) result[1]).intValue();
                    return new UserRankingResponse(user, rate);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.update(request.getNickname(), request.getBio(), request.getProfileImageUrl(), request.getBirthDate(),
                request.getGender());

        return new UserResponse(user);
    }

    private String parseDeviceInfo(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        if (userAgent.contains("Mobile")) {
            return "Mobile";
        } else if (userAgent.contains("Tablet")) {
            return "Tablet";
        } else {
            return "Desktop";
        }
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.withdraw();
    }
}
