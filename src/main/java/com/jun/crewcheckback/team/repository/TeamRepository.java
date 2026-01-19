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
            "AND (:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Team> findTeams(String category, String keyword, Pageable pageable);
}
