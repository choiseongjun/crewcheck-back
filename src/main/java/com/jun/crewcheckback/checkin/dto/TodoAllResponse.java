package com.jun.crewcheckback.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TodoAllResponse {
    private List<CheckInResponse> approvedCheckIns;
    private List<CheckInResponse> pendingCheckIns;
    private List<CheckInResponse> rejectedCheckIns;
}
