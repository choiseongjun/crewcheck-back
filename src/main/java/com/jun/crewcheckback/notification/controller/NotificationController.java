package com.jun.crewcheckback.notification.controller;

import com.jun.crewcheckback.global.common.ApiResponse;
import com.jun.crewcheckback.notification.dto.NotificationSettingResponse;
import com.jun.crewcheckback.notification.dto.NotificationSettingUpdateRequest;
import com.jun.crewcheckback.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> getSettings(
            @AuthenticationPrincipal UserDetails userDetails) {
        NotificationSettingResponse response = notificationService.getSettings(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingResponse>> updateSettings(
            @RequestBody NotificationSettingUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        NotificationSettingResponse response = notificationService.updateSettings(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
