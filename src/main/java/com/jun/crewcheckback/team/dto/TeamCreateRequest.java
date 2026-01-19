package com.jun.crewcheckback.team.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TeamCreateRequest {
    private String name;
    private String description;
    private String goal;
    private String introduce;
    private String imageUrl;
    private String category;
    private String grade;
    private Integer memberLimit;
    private Boolean isPublic;
    private Boolean requireApproval;
    private String frequency;
    private Integer durationDays;
    private String commonMission;
    private LocalDate startDate;
}
