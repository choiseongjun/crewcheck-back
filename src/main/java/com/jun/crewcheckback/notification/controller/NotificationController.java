//package com.jun.crewcheckback.notification.controller;
//
//import com.jun.crewcheckback.global.common.ApiResponse;
//import com.jun.crewcheckback.notification.dto.*;
//import com.jun.crewcheckback.notification.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    @PostMapping("/token")
//    public ResponseEntity<ApiResponse<Void>> registerToken(
//            @RequestBody DeviceTokenRegisterRequest request,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        notificationService.registerToken(userDetails.getUsername(), request);
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
//
//    @DeleteMapping("/token/{deviceId}")
//    public ResponseEntity<ApiResponse<Void>> removeToken(
//            @PathVariable String deviceId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        notificationService.removeToken(userDetails.getUsername(), deviceId);
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
//
//    @PostMapping("/send")
//    public ResponseEntity<ApiResponse<Void>> sendNotification(
//            @RequestBody SendNotificationRequest request) {
//
//        notificationService.sendToUsers(
//                request.userIds(),
//                request.title(),
//                request.body(),
//                request.notificationType(),
//                request.referenceId()
//        );
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
//
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        List<NotificationResponse> responses = notificationService.getNotifications(userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success(responses));
//    }
//
//    @GetMapping("/unread")
//    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        List<NotificationResponse> responses = notificationService.getUnreadNotifications(userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success(responses));
//    }
//
//    @GetMapping("/unread/count")
//    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        Long count = notificationService.getUnreadCount(userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success(new UnreadCountResponse(count)));
//    }
//
//    @PatchMapping("/{notificationId}/read")
//    public ResponseEntity<ApiResponse<Void>> markAsRead(
//            @PathVariable UUID notificationId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        notificationService.markAsRead(notificationId, userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
//
//    @PatchMapping("/read-all")
//    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        notificationService.markAllAsRead(userDetails.getUsername());
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
//}
