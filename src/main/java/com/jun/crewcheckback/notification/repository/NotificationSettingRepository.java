package com.jun.crewcheckback.notification.repository;

import com.jun.crewcheckback.notification.domain.NotificationSetting;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, UUID> {
    Optional<NotificationSetting> findByUser(User user);

    java.util.List<NotificationSetting> findAllByReminder09True();

    java.util.List<NotificationSetting> findAllByReminder12True();

    java.util.List<NotificationSetting> findAllByReminder18True();

    java.util.List<NotificationSetting> findAllByReminder21True();
}
