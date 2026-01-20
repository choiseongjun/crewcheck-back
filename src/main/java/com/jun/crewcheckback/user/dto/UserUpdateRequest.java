package com.jun.crewcheckback.user.dto;

import com.jun.crewcheckback.user.domain.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String nickname;
    private String bio;
    private String profileImageUrl;
    private LocalDate birthDate;
    private Gender gender;

    public UserUpdateRequest(String nickname, String bio, String profileImageUrl, LocalDate birthDate, Gender gender) {
        this.nickname = nickname;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.gender = gender;
    }
}
