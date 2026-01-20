package com.jun.crewcheckback.user.controller;

import com.jun.crewcheckback.global.common.ApiResponse;
import com.jun.crewcheckback.user.dto.*;
import com.jun.crewcheckback.user.dto.UserUpdateRequest;
import com.jun.crewcheckback.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final com.jun.crewcheckback.checkin.service.CheckInService checkInService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@RequestBody UserSignUpRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        TokenResponse response = userService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // @PostMapping("/logout")
    // public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest
    // request) {
    // userService.logout(request.getRefreshToken());
    // return ResponseEntity.ok(ApiResponse.success(null));
    // }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutAll(@AuthenticationPrincipal UserDetails userDetails) {
        userService.logoutAll(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @RequestBody ReissueRequest request) {
        TokenResponse response = userService.reissue(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<UserRankingResponse>>> getUserRanking() {
        List<UserRankingResponse> response = userService.getUserRanking();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{userId}/grass")
    public ResponseEntity<ApiResponse<java.util.List<com.jun.crewcheckback.checkin.dto.GrassResponse>>> getUserGrass(
            @PathVariable java.util.UUID userId,
            @RequestParam int year,
            @RequestParam int month) {
        java.util.List<com.jun.crewcheckback.checkin.dto.GrassResponse> response = checkInService.getUserGrass(userId,
                year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @org.springframework.web.bind.annotation.PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.updateUser(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
