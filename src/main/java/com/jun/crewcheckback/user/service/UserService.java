package com.jun.crewcheckback.user.service;

import com.jun.crewcheckback.global.security.JwtTokenProvider;
import com.jun.crewcheckback.user.domain.RefreshToken;
import com.jun.crewcheckback.user.dto.LoginRequest;
import com.jun.crewcheckback.user.dto.TokenResponse;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.dto.UserResponse;
import com.jun.crewcheckback.user.dto.UserSignUpRequest;
import com.jun.crewcheckback.user.repository.RefreshTokenRepository;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenValidityInMilliseconds;

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

        return new TokenResponse(accessToken, refreshTokenValue, "Bearer");
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
                .ipAddress(storedToken.getIpAddress()) // Reuse IP/UserAgent for simplicity or get from request if
                                                       // possible
                .userAgent(storedToken.getUserAgent())
                .deviceInfo(storedToken.getDeviceInfo())
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshTokenValue, "Bearer");
    }

    public UserResponse getUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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
}
