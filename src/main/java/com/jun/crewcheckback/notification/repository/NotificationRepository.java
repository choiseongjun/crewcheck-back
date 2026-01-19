package com.jun.crewcheckback.notification.repository;

import com.jun.crewcheckback.notification.domain.Notification;
import com.jun.crewcheckback.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.deletedYn = 'N' ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND n.deletedYn = 'N' ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.notificationType = :type AND n.deletedYn = 'N' ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") NotificationType type);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND n.deletedYn = 'N'")
    Long countUnreadByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") UUID userId);
}
