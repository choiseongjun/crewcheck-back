package com.jun.crewcheckback.user.dto;

import com.jun.crewcheckback.user.domain.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserResponse {
    private final UUID id;
    private final String email;
    private final String nickname;
    private final String bio;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.bio = user.getBio();
    }
}
