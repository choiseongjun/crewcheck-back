package com.jun.crewcheckback.notification.repository;

import com.jun.crewcheckback.notification.domain.NotificationSetting;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, UUID> {
    Optional<NotificationSetting> findByUser(User user);
}
