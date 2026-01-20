package com.jun.crewcheckback.checkin.controller;

import com.jun.crewcheckback.checkin.dto.*;
import com.jun.crewcheckback.checkin.service.CheckInService;
import com.jun.crewcheckback.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/check-ins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckInResponse>> createCheckIn(@RequestBody CheckInCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CheckInResponse response = checkInService.createCheckIn(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{checkInId}")
    public ResponseEntity<ApiResponse<CheckInResponse>> getCheckIn(@PathVariable UUID checkInId) {
        CheckInResponse response = checkInService.getCheckIn(checkInId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CheckInResponse>>> getCheckIns(@RequestParam UUID teamId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CheckInResponse> responses = checkInService.getCheckIns(teamId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{checkInId}")
    public ResponseEntity<ApiResponse<CheckInResponse>> updateCheckIn(@PathVariable UUID checkInId,
            @RequestBody CheckInUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CheckInResponse response = checkInService.updateCheckIn(checkInId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{checkInId}")
    public ResponseEntity<ApiResponse<Void>> deleteCheckIn(@PathVariable UUID checkInId,
            @AuthenticationPrincipal UserDetails userDetails) {
        checkInService.deleteCheckIn(checkInId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/todo")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodo(
            @RequestParam(required = false) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        TodoResponse response = checkInService.getTodo(userDetails.getUsername(),
                date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/todo/all")
    public ResponseEntity<ApiResponse<TodoAllResponse>> getTodoAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        TodoAllResponse response = checkInService
                .getTodoAll(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/todo/weekly")
    public ResponseEntity<ApiResponse<TodoAllResponse>> getWeeklyTodo(
            @RequestParam(required = false) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        TodoAllResponse response = checkInService.getWeeklyTodo(userDetails.getUsername(), date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/todo/monthly")
    public ResponseEntity<ApiResponse<TodoAllResponse>> getMonthlyTodo(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal UserDetails userDetails) {
        TodoAllResponse response = checkInService.getMonthlyTodo(userDetails.getUsername(), year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<StatsResponse>> getStats(
            @RequestParam String period,
            @AuthenticationPrincipal UserDetails userDetails) {
        StatsResponse response = checkInService.getStats(userDetails.getUsername(),
                period);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/grass")
    public ResponseEntity<ApiResponse<List<GrassResponse>>> getGrass(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GrassResponse> response = checkInService
                .getGrass(userDetails.getUsername(), year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<ApiResponse<DailyCheckInSummaryResponse>> getDailySummary(
            @RequestParam(required = false) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        DailyCheckInSummaryResponse response = checkInService.getDailySummary(userDetails.getUsername(), date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/streak")
    public ResponseEntity<ApiResponse<StreakResponse>> getStreak(
            @RequestParam UUID userId) {
        StreakResponse response = checkInService.getStreak(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
