package com.jun.crewcheckback.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatsResponse {
    private int teamCount;
    private int totalCheckInCount;
    private int currentStreak;

    public static UserStatsResponse of(int teamCount, int totalCheckInCount, int currentStreak) {
        return UserStatsResponse.builder()
                .teamCount(teamCount)
                .totalCheckInCount(totalCheckInCount)
                .currentStreak(currentStreak)
                .build();
    }
}
