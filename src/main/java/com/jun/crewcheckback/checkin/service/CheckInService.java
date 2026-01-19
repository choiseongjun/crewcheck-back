package com.jun.crewcheckback.checkin.service;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.checkin.dto.CheckInCreateRequest;
import com.jun.crewcheckback.checkin.dto.CheckInResponse;
import com.jun.crewcheckback.checkin.dto.CheckInUpdateRequest;
import com.jun.crewcheckback.checkin.repository.CheckInRepository;
import com.jun.crewcheckback.team.domain.Team;
import com.jun.crewcheckback.team.repository.TeamRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public CheckInResponse createCheckIn(CheckInCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Team team = teamRepository.findByIdAndDeletedYn(request.getTeamId(), "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        CheckIn checkIn = CheckIn.builder()
                .user(user)
                .team(team)
                .content(request.getContent())
                .difficultyLevel(request.getDifficultyLevel())
                .imageUrl(request.getImageUrl())
                .routineTitle(request.getRoutineTitle())
                .build();

        CheckIn savedCheckIn = checkInRepository.save(checkIn);
        return new CheckInResponse(savedCheckIn);
    }

    public CheckInResponse getCheckIn(UUID checkInId) {
        CheckIn checkIn = checkInRepository.findByIdAndDeletedYn(checkInId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Check-in not found"));
        return new CheckInResponse(checkIn);
    }

    public Page<CheckInResponse> getCheckIns(UUID teamId, Pageable pageable) {
        return checkInRepository.findAllByTeamIdAndDeletedYn(teamId, "N", pageable)
                .map(CheckInResponse::new);
    }

    @Transactional
    public CheckInResponse updateCheckIn(UUID checkInId, CheckInUpdateRequest request, String email) {
        CheckIn checkIn = checkInRepository.findByIdAndDeletedYn(checkInId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Check-in not found"));

        if (!checkIn.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Only author can update the check-in");
        }

        checkIn.update(request.getContent(), request.getImageUrl(), request.getRoutineTitle());
        return new CheckInResponse(checkIn);
    }

    @Transactional
    public void deleteCheckIn(UUID checkInId, String email) {
        CheckIn checkIn = checkInRepository.findByIdAndDeletedYn(checkInId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Check-in not found"));

        if (!checkIn.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Only author can delete the check-in");
        }

        checkIn.delete();
    }
}
