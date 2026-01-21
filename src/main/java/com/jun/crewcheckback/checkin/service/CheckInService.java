package com.jun.crewcheckback.checkin.service;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.checkin.dto.*;
import com.jun.crewcheckback.checkin.repository.CheckInRepository;
import com.jun.crewcheckback.team.domain.Team;
import com.jun.crewcheckback.team.domain.TeamMember;
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
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final com.jun.crewcheckback.team.repository.TeamMemberRepository teamMemberRepository;
    private final com.jun.crewcheckback.notification.service.NotificationService notificationService;

    @Transactional
    public CheckInResponse createCheckIn(CheckInCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Team team = teamRepository.findByIdAndDeletedYn(request.getTeamId(), "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        CheckIn checkIn = CheckIn.builder()
                .user(user)
                .team(team)
                .status("pending")
                .content(request.getContent())
                .difficultyLevel(request.getDifficultyLevel())
                .imageUrl(request.getImageUrl())
                .routineTitle(request.getRoutineTitle())
                .build();

        CheckIn savedCheckIn = checkInRepository.save(checkIn);


        // Send notification to team members
        List<User> recipients = teamMemberRepository.findAllByTeamId(team.getId()).stream()
                .map(com.jun.crewcheckback.team.domain.TeamMember::getUser)
                .collect(Collectors.toList());

        notificationService.sendCheckInNotificationInit(team, user, savedCheckIn, recipients);

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

        System.out.println("request===="+request.getStatus());

        checkIn.update(request.getContent(), request.getImageUrl(), request.getRoutineTitle(), request.getStatus(),
                request.getDifficultyLevel());



        //완료시에만 알림
        if(request.getStatus().equals("approved")) {
            // Send notification to team members
            List<User> recipients = teamMemberRepository.findAllByTeamId(checkIn.getTeam().getId()).stream()
                    .map(TeamMember::getUser)
                    .collect(Collectors.toList());
            notificationService.sendCheckInNotification(checkIn.getTeam(), checkIn.getUser(), checkIn, recipients);

            // 연속 달성 알림
            int streakDays = calculateStreakDays(checkIn.getUser());
            if (streakDays > 1) {
                notificationService.sendStreakNotification(checkIn.getUser(), streakDays);
            }
        }


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

        return getGrassForUser(user, year, month);
    }

    public List<GrassResponse> getUserGrass(UUID userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return getGrassForUser(user, year, month);
    }

    private List<GrassResponse> getGrassForUser(User user, int year, int month) {
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

    public TodoAllResponse getWeeklyTodo(String email, LocalDate date) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        // Adjust to Monday
        LocalDate startOfWeek = targetDate.minusDays(targetDate.getDayOfWeek().getValue() - 1);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = startOfWeek.plusDays(7).atStartOfDay().minusNanos(1);

        return getTodoAllByPeriod(user, start, end);
    }

    public TodoAllResponse getMonthlyTodo(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusNanos(1);

        return getTodoAllByPeriod(user, start, end);
    }

    public TodoAllResponse getUserWeeklyTodo(UUID userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDate startOfWeek = targetDate.minusDays(targetDate.getDayOfWeek().getValue() - 1);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = startOfWeek.plusDays(7).atStartOfDay().minusNanos(1);

        return getTodoAllByPeriod(user, start, end);
    }

    public TodoAllResponse getUserMonthlyTodo(UUID userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusNanos(1);

        return getTodoAllByPeriod(user, start, end);
    }

    public com.jun.crewcheckback.user.dto.AchievementRateResponse getUserWeeklyAchievementRate(UUID userId,
            LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDate startOfWeek = targetDate.minusDays(targetDate.getDayOfWeek().getValue() - 1);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = startOfWeek.plusDays(7).atStartOfDay().minusNanos(1);

        List<CheckIn> checkIns = checkInRepository.findAllByUserAndTimestampBetween(user, start, end);

        int totalCheckIns = checkIns.size();
        int approvedCount = (int) checkIns.stream()
                .filter(c -> "approved".equalsIgnoreCase(c.getStatus()))
                .count();

        double rate = totalCheckIns == 0 ? 0.0 : (double) approvedCount / totalCheckIns * 100;

        return new com.jun.crewcheckback.user.dto.AchievementRateResponse(rate, totalCheckIns, approvedCount);
    }

    public com.jun.crewcheckback.user.dto.AchievementRateResponse getUserTotalAchievementRate(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<CheckIn> checkIns = checkInRepository.findAllByUser(user);

        int totalCheckIns = checkIns.size();
        int approvedCount = (int) checkIns.stream()
                .filter(c -> "approved".equalsIgnoreCase(c.getStatus()))
                .count();

        double rate = totalCheckIns == 0 ? 0.0 : (double) approvedCount / totalCheckIns * 100;

        return new com.jun.crewcheckback.user.dto.AchievementRateResponse(rate, totalCheckIns, approvedCount);
    }

    private TodoAllResponse getTodoAllByPeriod(User user, LocalDateTime start, LocalDateTime end) {
        List<CheckIn> checkIns = checkInRepository.findAllByUserAndTimestampBetween(user, start, end);
        return mapCheckInsToTodoResponse(checkIns);
    }

    public TodoAllResponse getTeamMemberWeeklyTodo(UUID teamId, UUID userId, LocalDate date) {
        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDate startOfWeek = targetDate.minusDays(targetDate.getDayOfWeek().getValue() - 1);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = startOfWeek.plusDays(7).atStartOfDay().minusNanos(1);

        List<CheckIn> checkIns = checkInRepository.findAllByTeamAndUserAndTimestampBetween(team, user, start, end);
        return mapCheckInsToTodoResponse(checkIns);
    }

    public TodoAllResponse getTeamMemberMonthlyTodo(UUID teamId, UUID userId, int year, int month) {
        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusNanos(1);

        List<CheckIn> checkIns = checkInRepository.findAllByTeamAndUserAndTimestampBetween(team, user, start, end);
        return mapCheckInsToTodoResponse(checkIns);
    }

    public TodoAllResponse getTeamMemberDailyTodo(UUID teamId, UUID userId, LocalDate date) {
        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        List<CheckIn> checkIns = checkInRepository.findAllByTeamAndUserAndTimestampBetween(team, user, start, end);
        return mapCheckInsToTodoResponse(checkIns);
    }

    private int calculateStreakDays(User user) {
        List<CheckIn> approvedCheckIns = checkInRepository.findAllByUserAndStatusAndDeletedYn(user, "approved", "N");

        if (approvedCheckIns.isEmpty()) {
            return 0;
        }

        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        Set<LocalDate> approvedDates = approvedCheckIns.stream()
                .map(checkIn -> checkIn.getTimestamp().atZone(seoulZone).toLocalDate())
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now(seoulZone);
        int streak = 0;

        for (int i = 0; i < 365; i++) {
            LocalDate checkDate = today.minusDays(i);
            if (approvedDates.contains(checkDate)) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    private TodoAllResponse mapCheckInsToTodoResponse(List<CheckIn> checkIns) {
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
        return new TodoAllResponse(approvedCheckIns, pendingCheckIns, rejectedCheckIns);
    }

    public StreakResponse getStreak(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<CheckIn> approvedCheckIns = checkInRepository.findAllByUserAndStatusAndDeletedYn(user, "approved", "N");

        if (approvedCheckIns.isEmpty()) {
            return new StreakResponse(0);
        }

        // 날짜별로 승인된 체크인이 있는지 확인 (중복 날짜 제거)
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        Set<LocalDate> approvedDates = approvedCheckIns.stream()
                .map(checkIn -> checkIn.getTimestamp().atZone(seoulZone).toLocalDate())
                .collect(Collectors.toSet());

        // 가장 최근 approved 날짜 찾기
        LocalDate latestDate = approvedDates.stream()
                .max(LocalDate::compareTo)
                .orElse(null);

        if (latestDate == null) {
            return new StreakResponse(0);
        }

        int streak = 0;
        LocalDate checkDate = latestDate;

        // 최근 날짜부터 역순으로 연속 일수 계산
        while (approvedDates.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return new StreakResponse(streak);
    }
}
