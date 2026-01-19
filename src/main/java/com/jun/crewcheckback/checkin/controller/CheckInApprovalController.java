package com.jun.crewcheckback.checkin.controller;

import com.jun.crewcheckback.checkin.dto.CheckInApprovalRequest;
import com.jun.crewcheckback.checkin.dto.CheckInApprovalResponse;
import com.jun.crewcheckback.checkin.dto.CheckInResponse;
import com.jun.crewcheckback.checkin.service.CheckInApprovalService;
import com.jun.crewcheckback.global.common.ApiResponse;
import com.jun.crewcheckback.team.dto.TeamMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/check-in-approvals")
@RequiredArgsConstructor
public class CheckInApprovalController {

    private final CheckInApprovalService checkInApprovalService;
    private final com.jun.crewcheckback.team.service.TeamService teamService;

    @PostMapping("/{checkInId}")
    public ResponseEntity<ApiResponse<CheckInApprovalResponse>> createApproval(@PathVariable UUID checkInId,
            @RequestBody CheckInApprovalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CheckInApprovalResponse response = checkInApprovalService.createApproval(checkInId, request,
                userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{approvalId}")
    public ResponseEntity<ApiResponse<Void>> deleteApproval(@PathVariable UUID approvalId,
            @AuthenticationPrincipal UserDetails userDetails) {
        checkInApprovalService.deleteApproval(approvalId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getTeamMembers(
            @PathVariable UUID teamId) {
        List<TeamMemberResponse> response = teamService.getTeamMembers(teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/teams/{teamId}/today-approved")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getTodayApprovedCheckIns(
            @PathVariable UUID teamId) {
        List<CheckInResponse> response = checkInApprovalService
                .getTodayApprovedCheckIns(teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
