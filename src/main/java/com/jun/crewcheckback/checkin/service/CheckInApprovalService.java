package com.jun.crewcheckback.checkin.service;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.checkin.domain.CheckInApproval;
import com.jun.crewcheckback.checkin.dto.CheckInApprovalRequest;
import com.jun.crewcheckback.checkin.dto.CheckInApprovalResponse;
import com.jun.crewcheckback.checkin.repository.CheckInApprovalRepository;
import com.jun.crewcheckback.checkin.repository.CheckInRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckInApprovalService {

    private final CheckInApprovalRepository checkInApprovalRepository;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;

    @Transactional
    public CheckInApprovalResponse createApproval(UUID checkInId, CheckInApprovalRequest request, String email) {
        User approver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CheckIn checkIn = checkInRepository.findByIdAndDeletedYn(checkInId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Check-in not found"));

        // Only team owner can approve/reject? Assuming yes for now.
        if (!checkIn.getTeam().getOwner().equals(approver)) {
            // throw new IllegalArgumentException("Only team owner can approve/reject
            // check-ins");
            // For now, let's allow it or maybe check simple logic.
            // Let's enforce owner check.
            if (!checkIn.getTeam().getOwner().getId().equals(approver.getId())) {
                throw new IllegalArgumentException("Only team owner can approve/reject check-ins");
            }
        }

        checkIn.updateStatus(request.getStatus()); // 'approved' or 'rejected'

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

        approval.delete();
    }
}
