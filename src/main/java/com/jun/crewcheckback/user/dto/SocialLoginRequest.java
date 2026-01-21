package com.jun.crewcheckback.user.dto;

import com.jun.crewcheckback.user.domain.AuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SocialLoginRequest {
    private String email;
    private String nickname;
    private AuthProvider provider; // GOOGLE, APPLE, KAKAO
    private String socialId;
    private String profileImageUrl;
    private String deviceToken;
}
