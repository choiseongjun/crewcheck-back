package com.jun.crewcheckback.team.dto;

import com.jun.crewcheckback.team.domain.Team;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class TeamResponse {
    private UUID id;
    private String ownerName;
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
    private int achievementRate;
    private int currentMemberCount;

    public TeamResponse(Team team, int achievementRate, int currentMemberCount) {
        this.id = team.getId();
        this.ownerName = team.getOwner().getNickname();
        this.name = team.getName();
        this.description = team.getDescription();
        this.goal = team.getGoal();
        this.introduce = team.getIntroduce();
        this.imageUrl = team.getImageUrl();
        this.category = team.getCategory();
        this.grade = team.getGrade();
        this.memberLimit = team.getMemberLimit();
        this.isPublic = team.getIsPublic();
        this.requireApproval = team.getRequireApproval();
        this.frequency = team.getFrequency();
        this.durationDays = team.getDurationDays();
        this.commonMission = team.getCommonMission();
        this.startDate = team.getStartDate();
        this.achievementRate = achievementRate;
        this.currentMemberCount = currentMemberCount;
    }

    public TeamResponse(Team team, int achievementRate) {
        this(team, achievementRate, 0);
    }

    public TeamResponse(Team team) {
        this(team, 0, 0);
    }
}
