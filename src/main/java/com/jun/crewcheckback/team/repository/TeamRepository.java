package com.jun.crewcheckback.team.repository;

import com.jun.crewcheckback.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, UUID> {
        Optional<Team> findByIdAndDeletedYn(UUID id, String deletedYn);

        @org.springframework.data.jpa.repository.Query("SELECT t FROM Team t WHERE t.deletedYn = 'N' " +
                        "AND (:category IS NULL OR :category = '전체' OR t.category = :category) " +
                        "AND (:keyword IS NULL OR LOWER(t.name) LIKE :keyword)")
        Page<Team> findTeams(@org.springframework.data.repository.query.Param("category") String category,
                        @org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

        @org.springframework.data.jpa.repository.Query("SELECT t as team, " +
                        "COALESCE(CAST(SUM(CASE WHEN c.status = 'approved' THEN 1 ELSE 0 END) AS double) / NULLIF(COUNT(c.id), 0) * 100, 0) as rate "
                        +
                        "FROM Team t " +
                        "LEFT JOIN CheckIn c ON c.team = t " +
                        "WHERE t.deletedYn = 'N' " +
                        "GROUP BY t " +
                        "ORDER BY rate DESC")
        java.util.List<Object[]> findTeamAchievementRates(Pageable pageable);
}
