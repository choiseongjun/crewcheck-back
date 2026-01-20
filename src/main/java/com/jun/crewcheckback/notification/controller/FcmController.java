package com.jun.crewcheckback.notification.controller;

import com.jun.crewcheckback.global.common.ApiResponse;
import com.jun.crewcheckback.notification.dto.FcmSendRequest;
import com.jun.crewcheckback.notification.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Tag(name = "FCM Test", description = "FCM 푸시 알림 테스트 API")
@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;
    private final com.jun.crewcheckback.user.repository.UserRepository userRepository;
    private final com.jun.crewcheckback.team.repository.TeamRepository teamRepository;
    private final com.jun.crewcheckback.team.repository.TeamMemberRepository teamMemberRepository;
    private final com.jun.crewcheckback.notification.service.NotificationService notificationService;

    // @Operation(summary = "푸시 알림 전송 테스트", description = "특정 사용자에게 푸시 알림을 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendNotification(@RequestBody FcmSendRequest request) {
        com.jun.crewcheckback.user.domain.User user = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getDeviceToken() == null || user.getDeviceToken().isEmpty()) {
            throw new IllegalArgumentException("사용자의 디바이스 토큰이 존재하지 않습니다.");
        }

        String response = fcmService.sendMessage(user.getDeviceToken(), request.getTitle(), request.getBody());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // @Operation(summary = "팀 가입 알림 테스트", description = "팀 가입 시나리오를 가정하여 알림을
    // 테스트합니다.")
    @PostMapping("/test-team-join")
    public ResponseEntity<ApiResponse<String>> testTeamJoinNotification(
            @RequestBody com.jun.crewcheckback.notification.dto.TestTeamJoinRequest request) {
        com.jun.crewcheckback.team.domain.Team team = teamRepository.findByIdAndDeletedYn(request.getTeamId(), "N")
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        com.jun.crewcheckback.user.domain.User joiner = userRepository.findById(request.getJoinerId())
                .orElseThrow(() -> new IllegalArgumentException("가입자를 찾을 수 없습니다."));

        java.util.List<com.jun.crewcheckback.user.domain.User> recipients = teamMemberRepository
                .findAllByTeamId(team.getId()).stream()
                .map(com.jun.crewcheckback.team.domain.TeamMember::getUser)
                .filter(member -> !member.getId().equals(joiner.getId()))
                .collect(java.util.stream.Collectors.toList());

        notificationService.sendNewMemberNotification(team, joiner, recipients);

        return ResponseEntity.ok(ApiResponse.success(recipients.size() + "명의 팀원에게 알림을 전송했습니다."));
    }
}
