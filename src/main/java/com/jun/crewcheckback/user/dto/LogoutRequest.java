package com.jun.crewcheckback.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequest {
    private String refreshToken;
}
