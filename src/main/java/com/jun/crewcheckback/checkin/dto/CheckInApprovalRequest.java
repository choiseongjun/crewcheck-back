package com.jun.crewcheckback.checkin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckInApprovalRequest {
    private String status; // 'approved' or 'rejected'
}
