package com.jun.crewcheckback.user.repository;

import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT u as user, " +
            "COALESCE(CAST(SUM(CASE WHEN c.status = 'approved' THEN 1 ELSE 0 END) AS double) / NULLIF(COUNT(c.id), 0) * 100, 0) as rate "
            +
            "FROM User u " +
            "LEFT JOIN CheckIn c ON c.user = u " +
            "WHERE u.deletedYn = 'N' " +
            "GROUP BY u " +
            "ORDER BY rate DESC")
    java.util.List<Object[]> findUserAchievementRates(org.springframework.data.domain.Pageable pageable);
}
