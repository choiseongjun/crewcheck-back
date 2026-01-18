package com.jun.crewcheckback.user.dto;

import com.jun.crewcheckback.user.domain.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequest {
    private String email;
    private String password;
    private String nickname;
    private String bio;
    private String profileImageUrl;
    private LocalDate birthDate;
    private Gender gender;
}
