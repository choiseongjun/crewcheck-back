package com.jun.crewcheckback.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class TestTeamJoinRequest {
    private UUID teamId;
    private UUID joinerId;
}
