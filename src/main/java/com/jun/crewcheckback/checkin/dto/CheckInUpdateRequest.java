package com.jun.crewcheckback.checkin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckInUpdateRequest {
    private String content;
    private String imageUrl;
    private String status;
    private String routineTitle;
    private String difficultyLevel;
}
