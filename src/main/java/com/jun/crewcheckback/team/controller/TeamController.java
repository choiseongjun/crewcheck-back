package com.jun.crewcheckback.team.controller;

import com.jun.crewcheckback.global.common.ApiResponse;
import com.jun.crewcheckback.team.dto.TeamCreateRequest;
import com.jun.crewcheckback.team.dto.TeamResponse;
import com.jun.crewcheckback.team.dto.TeamUpdateRequest;
import com.jun.crewcheckback.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@RequestBody TeamCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TeamResponse response = teamService.createTeam(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(@PathVariable UUID teamId) {
        TeamResponse response = teamService.getTeam(teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TeamResponse>>> getTeams(@PageableDefault(size = 10) Pageable pageable) {
        Page<TeamResponse> responses = teamService.getTeams(pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(@PathVariable UUID teamId,
            @RequestBody TeamUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TeamResponse response = teamService.updateTeam(teamId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable UUID teamId,
            @AuthenticationPrincipal UserDetails userDetails) {
        teamService.deleteTeam(teamId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<com.jun.crewcheckback.team.dto.TeamMemberResponse>> joinTeam(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal UserDetails userDetails) {
        com.jun.crewcheckback.team.dto.TeamMemberResponse response = teamService.joinTeam(teamId,
                userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<Void>> leaveTeam(@PathVariable UUID teamId,
            @AuthenticationPrincipal UserDetails userDetails) {
        teamService.leaveTeam(teamId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
