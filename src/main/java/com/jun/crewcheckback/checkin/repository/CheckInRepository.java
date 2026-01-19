package com.jun.crewcheckback.checkin.repository;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import com.jun.crewcheckback.team.domain.Team;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {
    Optional<CheckIn> findByIdAndDeletedYn(UUID id, String deletedYn);

    Page<CheckIn> findAllByDeletedYn(String deletedYn, Pageable pageable);

    Page<CheckIn> findAllByTeamIdAndDeletedYn(UUID teamId, String deletedYn, Pageable pageable);

    boolean existsByTeamAndUserAndTimestampBetween(Team team, User user, LocalDateTime start, LocalDateTime end);

    List<CheckIn> findAllByUserAndTimestampBetween(User user, LocalDateTime start, LocalDateTime end);

    boolean existsByTeamAndUserAndTimestampBetweenAndStatus(Team team, User user, LocalDateTime start, LocalDateTime end, String status);

    List<CheckIn> findAllByUserAndTimestampBetweenAndStatus(User user, LocalDateTime start, LocalDateTime end, String status);

    List<CheckIn> findAllByUser(User user);
}
