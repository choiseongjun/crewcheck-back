package com.jun.crewcheckback.notification.service;

import com.jun.crewcheckback.notification.domain.Notification;
import com.jun.crewcheckback.notification.domain.NotificationSetting;
import com.jun.crewcheckback.notification.domain.NotificationType;
import com.jun.crewcheckback.notification.dto.NotificationSettingResponse;
import com.jun.crewcheckback.notification.dto.NotificationSettingUpdateRequest;
import com.jun.crewcheckback.notification.repository.NotificationSettingRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

        private final NotificationSettingRepository notificationSettingRepository;
        private final com.jun.crewcheckback.notification.repository.NotificationRepository notificationRepository;
        private final UserRepository userRepository;
        private final FcmService fcmService;

        @Transactional
        public void sendNewMemberNotification(com.jun.crewcheckback.team.domain.Team team, User newMember,
                        List<User> recipients) {
                String title = "새로운 팀원 알림";
                String body = String.format("%s 팀에 %s 님이 합류했습니다!", team.getName(), newMember.getNickname());

                for (User recipient : recipients) {
                        NotificationSetting setting = notificationSettingRepository.findByUser(recipient)
                                        .orElse(NotificationSetting.createDefault(recipient));

                        if (setting.isNewMemberNotification()) {
                                sendNotification(recipient, title, body, NotificationType.TEAM, team.getId());
                        }
                }
        }

        @Transactional
        public void sendCheckInNotificationInit(com.jun.crewcheckback.team.domain.Team team, User user,
                                            com.jun.crewcheckback.checkin.domain.CheckIn checkIn, List<User> recipients) {
                String title = "팀 활동 알림";
                String body = String.format("%s 님이 '%s' 루틴을 올렸습니다!", user.getNickname(), checkIn.getRoutineTitle());

                for (User recipient : recipients) {
                        NotificationSetting setting = notificationSettingRepository.findByUser(recipient)
                                .orElse(NotificationSetting.createDefault(recipient));

                        if (setting.isTeamActivityNotification()) {
                                sendNotification(recipient, title, body, NotificationType.TEAM, checkIn.getId());
                        }
                }
        }
        @Transactional
        public void sendCheckInNotification(com.jun.crewcheckback.team.domain.Team team, User user,
                        com.jun.crewcheckback.checkin.domain.CheckIn checkIn, List<User> recipients) {
                String title = "팀 활동 알림";
                String body = String.format("%s 님이 '%s' 루틴을 완료했습니다!", user.getNickname(), checkIn.getRoutineTitle());

                for (User recipient : recipients) {
                        NotificationSetting setting = notificationSettingRepository.findByUser(recipient)
                                        .orElse(NotificationSetting.createDefault(recipient));

                        if (setting.isTeamActivityNotification()) {
                                sendNotification(recipient, title, body, NotificationType.TEAM, checkIn.getId());
                        }
                }
        }

        @Transactional
        public void sendKickNotification(com.jun.crewcheckback.team.domain.Team team, User user, String reason) {
                String title = "팀 강퇴 알림";
                String body = String.format("%s 팀에서 강퇴되었습니다. 사유: %s", team.getName(), reason);

                // Notification setting check? Maybe force send or check generic notification
                // setting?
                // Assuming always send for kick or check newMemberNotification?? No, maybe just
                // send.
                // Or check teamActivityNotification? It's severe, so maybe always send.
                // But let's check basic existence of setting.
                notificationSettingRepository.findByUser(user)
                                .orElse(NotificationSetting.createDefault(user));

                // We'll send it regardless of specific flag because it's an important account
                // action,
                // or reusing 'teamActivityNotification' if strictly needed.
                // Let's assume always send for now as per requirement "notification with
                // reason".

                sendNotification(user, title, body, NotificationType.TEAM, team.getId());
        }

        private void sendNotification(User recipient, String title, String body, NotificationType type,
                        java.util.UUID referenceId) {
                Notification notification = Notification
                                .builder()
                                .user(recipient)
                                .title(title)
                                .body(body)
                                .notificationType(type)
                                .referenceId(referenceId)
                                .build();

                // FCM Push
                if (recipient.getDeviceToken() != null && !recipient.getDeviceToken().isEmpty()) {
                        try {
                                fcmService.sendMessage(recipient.getDeviceToken(), title, body);
                                notification.markAsSent();
                        } catch (Exception e) {
                                // FCM 전송 실패 시 로그만 남기고 알림은 저장
                                // Consider logging the exception here, e.g., log.error("FCM send failed", e);
                        }
                }
                notificationRepository.save(notification);
        }

        public List<com.jun.crewcheckback.notification.dto.NotificationResponse> getNotifications(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                                .map(com.jun.crewcheckback.notification.dto.NotificationResponse::from)
                                .collect(java.util.stream.Collectors.toList());
        }

        @Transactional
        public NotificationSettingResponse getSettings(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                NotificationSetting setting = notificationSettingRepository.findByUser(user)
                                .orElseGet(() -> notificationSettingRepository
                                                .save(NotificationSetting.createDefault(user)));

                return new NotificationSettingResponse(setting);
        }

        @Transactional
        public NotificationSettingResponse updateSettings(String email, NotificationSettingUpdateRequest request) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                NotificationSetting setting = notificationSettingRepository.findByUser(user)
                                .orElseGet(() -> notificationSettingRepository
                                                .save(NotificationSetting.createDefault(user)));

                setting.update(
                                request.checkInReminder(),
                                request.teamActivityNotification(),
                                request.streakNotification(),
                                request.newMemberNotification(),
                                request.achievementNotification(),
                                request.doNotDisturbStart(),
                                request.doNotDisturbEnd(),
                                request.reminder09(),
                                request.reminder12(),
                                request.reminder18(),
                                request.reminder21());

                return new NotificationSettingResponse(setting);
        }
}
