package com.jun.crewcheckback.user.service;

import com.jun.crewcheckback.global.security.JwtTokenProvider;
import com.jun.crewcheckback.user.dto.LoginRequest;
import com.jun.crewcheckback.user.dto.TokenResponse;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.dto.UserResponse;
import com.jun.crewcheckback.user.dto.UserSignUpRequest;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtTokenProvider.createToken(user.getEmail());
        return new TokenResponse(token, "Bearer");
    }
}
