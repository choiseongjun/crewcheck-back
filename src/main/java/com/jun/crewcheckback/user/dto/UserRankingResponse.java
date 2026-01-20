package com.jun.crewcheckback.user.dto;

import com.jun.crewcheckback.user.domain.User;
import lombok.Getter;

@Getter
public class UserRankingResponse extends UserResponse {
    private final int achievementRate;

    public UserRankingResponse(User user, int achievementRate) {
        super(user);
        this.achievementRate = achievementRate;
    }
}
