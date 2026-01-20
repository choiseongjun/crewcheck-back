package com.jun.crewcheckback.user.dto;

public record AchievementRateResponse(
        double achievementRate,
        int totalCheckIns,
        int approvedCount) {
}
