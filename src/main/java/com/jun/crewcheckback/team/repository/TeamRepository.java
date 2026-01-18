package com.jun.crewcheckback.team.repository;

import com.jun.crewcheckback.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    Optional<Team> findByIdAndDeletedYn(UUID id, String deletedYn);

    Page<Team> findAllByDeletedYn(String deletedYn, Pageable pageable);
}
