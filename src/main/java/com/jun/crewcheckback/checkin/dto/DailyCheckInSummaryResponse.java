package com.jun.crewcheckback.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailyCheckInSummaryResponse {
    private long completedCount;
    private List<CheckInResponse> checkIns;
}
