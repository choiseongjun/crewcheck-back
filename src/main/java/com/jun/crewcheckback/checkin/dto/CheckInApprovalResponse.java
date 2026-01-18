package com.jun.crewcheckback.checkin.dto;

import com.jun.crewcheckback.checkin.domain.CheckInApproval;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CheckInApprovalResponse {
    private UUID id;
    private UUID checkInId;
    private String approverName;
    private String status;

    public CheckInApprovalResponse(CheckInApproval approval) {
        this.id = approval.getId();
        this.checkInId = approval.getCheckIn().getId();
        this.approverName = approval.getApprover().getNickname();
        this.status = approval.getCheckIn().getStatus();
    }
}
