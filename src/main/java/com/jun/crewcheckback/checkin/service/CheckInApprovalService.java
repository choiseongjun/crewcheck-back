package com.jun.crewcheckback.checkin.service;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.checkin.domain.CheckInApproval;
import com.jun.crewcheckback.checkin.dto.CheckInApprovalRequest;
import com.jun.crewcheckback.checkin.dto.CheckInApprovalResponse;
import com.jun.crewcheckback.checkin.dto.CheckInResponse;
import com.jun.crewcheckback.checkin.repository.CheckInApprovalRepository;
import com.jun.crewcheckback.checkin.repository.CheckInRepository;
import com.jun.crewcheckback.team.domain.Team;
import com.jun.crewcheckback.team.repository.TeamRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckInApprovalService {

    private final CheckInApprovalRepository checkInApprovalRepository;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final com.jun.crewcheckback.team.repository.TeamMemberRepository teamMemberRepository;

    @Transactional
    public CheckInApprovalResponse createApproval(UUID checkInId, CheckInApprovalRequest request, String email) {
        User approver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CheckIn checkIn = checkInRepository.findByIdAndDeletedYn(checkInId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Check-in not found"));

        // 1. Self-approval check: Approver cannot be the same as the check-in creator
        if (checkIn.getUser().getId().equals(approver.getId())) {
            throw new IllegalArgumentException("Cannot approve your own check-in");
        }

        // 2. Team membership check: Approver must be a member of the team
        teamMemberRepository.findByTeamAndUser(checkIn.getTeam(), approver)
                .orElseThrow(() -> new IllegalArgumentException("Only team members can approve check-ins"));

//        checkIn.updateStatus(request.getStatus()); // 'approved' or 'rejected'
//        checkInRepository.save(checkIn); // Explicitly save to ensure status update is persisted

        CheckInApproval approval = CheckInApproval.builder()
                .checkIn(checkIn)
                .approver(approver)
                .build();

        CheckInApproval savedApproval = checkInApprovalRepository.save(approval);
        return new CheckInApprovalResponse(savedApproval);
    }

    @Transactional
    public void deleteApproval(UUID approvalId, String email) {
        CheckInApproval approval = checkInApprovalRepository.findByIdAndDeletedYn(approvalId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Approval not found"));

        if (!approval.getApprover().getEmail().equals(email)) {
            throw new IllegalArgumentException("Only approver can delete the approval");
        }

        // Revert check-in status to pending
        approval.getCheckIn().updateStatus("pending");
        checkInRepository.save(approval.getCheckIn()); // Explicitly save status revert

        approval.delete();
    }

    public List<CheckInResponse> getTodayApprovedCheckIns(UUID teamId) {
        // Verify team exists
        teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        LocalDateTime start = LocalDateTime.of(LocalDate.now(java.time.ZoneId.of("Asia/Seoul")), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(java.time.ZoneId.of("Asia/Seoul")), LocalTime.MAX);

        // Fetch all check-ins for the team today, then filter by status 'approved'
        // (case-insensitive)
        return checkInRepository.findAllByTeamIdAndTimestampBetween(teamId, start, end).stream()
                .filter(c -> "approved".equalsIgnoreCase(c.getStatus()))
                .map(checkIn -> {
                    List<CheckInApproval> approvals = checkInApprovalRepository.findByCheckInIdAndDeletedYn(checkIn.getId(), "N");
                    return new CheckInResponse(checkIn, approvals);
                })
                .collect(Collectors.toList());
    }
}
