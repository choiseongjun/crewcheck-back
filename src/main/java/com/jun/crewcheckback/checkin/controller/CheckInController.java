package com.jun.crewcheckback.checkin.controller;

import com.jun.crewcheckback.checkin.dto.CheckInCreateRequest;
import com.jun.crewcheckback.checkin.dto.CheckInResponse;
import com.jun.crewcheckback.checkin.dto.CheckInUpdateRequest;
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
}
