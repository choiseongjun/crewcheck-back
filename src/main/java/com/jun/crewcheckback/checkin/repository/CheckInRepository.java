package com.jun.crewcheckback.checkin.repository;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {
    Optional<CheckIn> findByIdAndDeletedYn(UUID id, String deletedYn);

    Page<CheckIn> findAllByDeletedYn(String deletedYn, Pageable pageable);

    Page<CheckIn> findAllByTeamIdAndDeletedYn(UUID teamId, String deletedYn, Pageable pageable);
}
