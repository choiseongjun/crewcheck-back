package com.jun.crewcheckback.feed.repository;

import com.jun.crewcheckback.feed.domain.Feed;
import com.jun.crewcheckback.feed.domain.FeedSubject;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {
    Page<Feed> findAllByFeedSubjectAndDeletedYn(FeedSubject feedSubject, String deletedYn, Pageable pageable);

    Page<Feed> findAllByDeletedYn(String deletedYn, Pageable pageable);

    Optional<Feed> findByIdAndDeletedYn(UUID id, String deletedYn);

    int countByUserAndDeletedYnAndCreatedAtBetween(User user, String deletedYn, LocalDateTime start, LocalDateTime end);

    int countByUserAndDeletedYn(User user, String deletedYn);

    @Query("SELECT DISTINCT CAST(f.createdAt AS LocalDate) FROM Feed f WHERE f.user = :user AND f.deletedYn = 'N' ORDER BY CAST(f.createdAt AS LocalDate) DESC")
    List<LocalDate> findDistinctDatesByUser(@Param("user") User user);

    @Query("SELECT CAST(f.createdAt AS LocalDate) as date, COUNT(f) as count FROM Feed f " +
            "WHERE f.user = :user AND f.deletedYn = 'N' AND f.createdAt >= :startDate " +
            "GROUP BY CAST(f.createdAt AS LocalDate) ORDER BY date DESC")
    List<Object[]> countByUserGroupByDate(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT TO_CHAR(f.createdAt, 'YYYY-MM') as month, COUNT(f) as count FROM Feed f " +
            "WHERE f.user = :user AND f.deletedYn = 'N' AND f.createdAt < :beforeDate " +
            "GROUP BY TO_CHAR(f.createdAt, 'YYYY-MM') ORDER BY month DESC")
    List<Object[]> countByUserGroupByMonth(@Param("user") User user, @Param("beforeDate") LocalDateTime beforeDate);
}
