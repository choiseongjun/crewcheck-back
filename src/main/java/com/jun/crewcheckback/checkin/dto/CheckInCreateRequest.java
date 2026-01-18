package com.jun.crewcheckback.checkin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CheckInCreateRequest {
    private UUID teamId;
    private String content;
    private String imageUrl;
    private String routineTitle;
}
