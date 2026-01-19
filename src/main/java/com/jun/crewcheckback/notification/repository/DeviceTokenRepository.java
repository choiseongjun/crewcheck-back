package com.jun.crewcheckback.notification.repository;

import com.jun.crewcheckback.notification.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    @Query("SELECT dt FROM DeviceToken dt WHERE dt.user.id = :userId AND dt.isActive = true AND dt.deletedYn = 'N'")
    List<DeviceToken> findActiveTokensByUserId(@Param("userId") UUID userId);

    @Query("SELECT dt FROM DeviceToken dt WHERE dt.user.id = :userId AND dt.deviceId = :deviceId AND dt.deletedYn = 'N'")
    Optional<DeviceToken> findByUserIdAndDeviceId(@Param("userId") UUID userId, @Param("deviceId") String deviceId);

    @Query("SELECT dt FROM DeviceToken dt WHERE dt.fcmToken = :fcmToken AND dt.deletedYn = 'N'")
    Optional<DeviceToken> findByFcmToken(@Param("fcmToken") String fcmToken);

    @Query("SELECT dt.fcmToken FROM DeviceToken dt WHERE dt.user.id IN :userIds AND dt.isActive = true AND dt.deletedYn = 'N'")
    List<String> findActiveTokensByUserIds(@Param("userIds") List<UUID> userIds);
}
