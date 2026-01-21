package com.jun.crewcheckback.notification.scheduler;

import com.jun.crewcheckback.notification.domain.Notification;
import com.jun.crewcheckback.notification.domain.NotificationSetting;
import com.jun.crewcheckback.notification.domain.NotificationType;
import com.jun.crewcheckback.notification.repository.NotificationRepository;
import com.jun.crewcheckback.notification.repository.NotificationSettingRepository;
import com.jun.crewcheckback.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendReminder09() {
        sendReminders(notificationSettingRepository.findAllByReminder09True(), "오전 루틴 체크 알림",
                "상쾌한 아침! 오늘의 루틴을 시작해보세요.");
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void sendReminder12() {
        sendReminders(notificationSettingRepository.findAllByReminder12True(), "점심 루틴 체크 알림",
                "맛점 하셨나요? 오후도 힘내서 루틴을 챙겨봐요!");
    }

    @Scheduled(cron = "0 0 18 * * *")
    @Transactional
    public void sendReminder18() {
        sendReminders(notificationSettingRepository.findAllByReminder18True(), "저녁 루틴 체크 알림",
                "일과 후, 나만의 루틴으로 하루를 마무리해보세요.");
    }

    @Scheduled(cron = "0 0 21 * * *")
    @Transactional
    public void sendReminder21() {
        sendReminders(notificationSettingRepository.findAllByReminder21True(), "취침 전 루틴 체크 알림",
                "오늘 하루도 수고 많으셨어요! 자기 전 루틴 잊지 마세요.");
    }

    private void sendReminders(List<NotificationSetting> settings, String title, String body) {
        for (NotificationSetting setting : settings) {
            Notification notification = Notification.builder()
                    .user(setting.getUser())
                    .title(title)
                    .body(body)
                    .notificationType(NotificationType.TEAM) // Using TEAM as a generic type for now or create REMINDER
                                                             // type
                    .build();

            // FCM Push
            if (setting.getUser().getDeviceToken() != null && !setting.getUser().getDeviceToken().isEmpty()) {
                try {
                    fcmService.sendMessage(setting.getUser().getDeviceToken(), title, body);
                    notification.markAsSent();
                } catch (Exception e) {
                    // Log error
                }
            }
            notificationRepository.save(notification);
        }
    }
}
