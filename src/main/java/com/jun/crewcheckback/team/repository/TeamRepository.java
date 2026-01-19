package com.jun.crewcheckback.team.repository;

import com.jun.crewcheckback.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    Optional<Team> findByIdAndDeletedYn(UUID id, String deletedYn);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM teams t WHERE t.deleted_yn = 'N' " +
            "AND (:category IS NULL OR :category = '전체' OR t.category = :category) " +
            "AND (:keyword IS NULL OR t.name ILIKE :keyword)", countQuery = "SELECT count(*) FROM teams t WHERE t.deleted_yn = 'N' "
                    +
                    "AND (:category IS NULL OR :category = '전체' OR t.category = :category) " +
                    "AND (:keyword IS NULL OR t.name ILIKE :keyword)", nativeQuery = true)
    Page<Team> findTeams(String category, String keyword, Pageable pageable);
}
