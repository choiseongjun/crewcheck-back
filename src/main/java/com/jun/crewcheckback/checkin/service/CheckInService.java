package com.jun.crewcheckback.checkin.service;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.checkin.dto.*;
import com.jun.crewcheckback.checkin.repository.CheckInRepository;
import com.jun.crewcheckback.team.domain.Team;
import com.jun.crewcheckback.team.dto.TeamResponse;
import com.jun.crewcheckback.team.repository.TeamRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

        checkIn.update(request.getContent(), request.getImageUrl(), request.getRoutineTitle(), request.getStatus(),
                request.getDifficultyLevel());
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

    public TodoResponse getTodo(String email, LocalDate date) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        // Fetch check-in history directly instead of iterating all teams
        List<CheckIn> checkIns = checkInRepository.findAllByUserAndTimestampBetween(user, startOfDay, endOfDay);

        List<TeamResponse> approvedTeams = new ArrayList<>();
        List<TeamResponse> pendingTeams = new ArrayList<>();
        List<TeamResponse> rejectedTeams = new ArrayList<>();

        for (CheckIn checkIn : checkIns) {
            Team team = checkIn.getTeam();
            switch (checkIn.getStatus()) {
                case "approved":
                    approvedTeams.add(new TeamResponse(team));
                    break;
                case "rejected":
                    rejectedTeams.add(new TeamResponse(team));
                    break;
                case "pending":
                default:
                    pendingTeams.add(new TeamResponse(team));
                    break;
            }
        }

        return new TodoResponse(approvedTeams, pendingTeams, rejectedTeams);
    }

    public StatsResponse getStats(String email, String period) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        if ("WEEK".equalsIgnoreCase(period)) {
            start = end.minusWeeks(1);
        } else if ("MONTH".equalsIgnoreCase(period)) {
            start = end.minusMonths(1);
        } else {
            throw new IllegalArgumentException("Invalid period: " + period);
        }

        List<CheckIn> checkIns = checkInRepository.findAllByUserAndTimestampBetween(user, start, end);
        return new StatsResponse(checkIns.size());
    }

    public List<GrassResponse> getGrass(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        java.time.LocalDateTime start = java.time.LocalDateTime.of(year, month, 1, 0, 0);
        java.time.LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        List<CheckIn> checkIns = checkInRepository.findAllByUserAndTimestampBetweenAndStatus(user, start, end,
                "approved");

        Map<LocalDate, Long> counts = checkIns.stream()
                .collect(Collectors.groupingBy(
                        checkIn -> checkIn.getCreatedAt().toLocalDate(),
                        Collectors.counting()));

        return counts.entrySet().stream()
                .map(entry -> new GrassResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(GrassResponse::getDate))
                .collect(Collectors.toList());
    }

    public TodoAllResponse getTodoAll(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch check-in history directly instead of iterating all teams
        List<CheckIn> checkIns = checkInRepository.findAllByUser(user);

        List<CheckInResponse> approvedCheckIns = new ArrayList<>();
        List<CheckInResponse> pendingCheckIns = new ArrayList<>();
        List<CheckInResponse> rejectedCheckIns = new ArrayList<>();

        for (CheckIn checkIn : checkIns) {
            switch (checkIn.getStatus()) {
                case "approved":
                    approvedCheckIns.add(new CheckInResponse(checkIn));
                    break;
                case "rejected":
                    rejectedCheckIns.add(new CheckInResponse(checkIn));
                    break;
                case "pending":
                default:
                    pendingCheckIns.add(new CheckInResponse(checkIn));
                    break;
            }
        }

        return new TodoAllResponse(approvedCheckIns, pendingCheckIns,
                rejectedCheckIns);
    }

    public DailyCheckInSummaryResponse getDailySummary(String email, LocalDate date) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        List<CheckIn> checkIns = checkInRepository.findAllByUserAndTimestampBetween(user, start, end);

        long completedCount = checkIns.stream()
                .filter(checkIn -> "approved".equals(checkIn.getStatus()))
                .count();

        List<CheckInResponse> checkInResponses = checkIns.stream()
                .map(CheckInResponse::new)
                .collect(Collectors.toList());

        return new DailyCheckInSummaryResponse(completedCount, checkInResponses);
    }
}
