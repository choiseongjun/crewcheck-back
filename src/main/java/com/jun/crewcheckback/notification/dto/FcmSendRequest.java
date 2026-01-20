package com.jun.crewcheckback.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class FcmSendRequest {
    private UUID targetUserId;
    private String title;
    private String body;
}
