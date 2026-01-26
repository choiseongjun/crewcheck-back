package com.jun.crewcheckback.feed.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedStatisticsResponse {
    private int todayCount;
    private int streakDays;
    private int totalCount;
    private List<DailyCount> dailyCounts;
    private List<MonthlyCount> monthlyCounts;

    @Getter
    @Builder
    public static class DailyCount {
        private String date;
        private int count;
    }

    @Getter
    @Builder
    public static class MonthlyCount {
        private String month;
        private int count;
    }
}
