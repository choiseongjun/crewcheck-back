package com.jun.crewcheckback.checkin.dto;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CheckInResponse {
    private UUID id;
    private String userName;
    private String teamName;
    private String content;
    private String imageUrl;
    private String routineTitle;
    private String status;
    private LocalDateTime timestamp;

    public CheckInResponse(CheckIn checkIn) {
        this.id = checkIn.getId();
        this.userName = checkIn.getUser().getNickname();
        this.teamName = checkIn.getTeam().getName();
        this.content = checkIn.getContent();
        this.imageUrl = checkIn.getImageUrl();
        this.routineTitle = checkIn.getRoutineTitle();
        this.status = checkIn.getStatus();
        this.timestamp = checkIn.getTimestamp();
    }
}
