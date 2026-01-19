package com.jun.crewcheckback.team.dto;

import com.jun.crewcheckback.team.domain.TeamMember;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TeamMemberResponse {
    private UUID userId;
    private String nickname;
    private String email;
    private String profileImage;
    private String role;
    private String status;
    private LocalDateTime joinedAt;

    public TeamMemberResponse(TeamMember teamMember) {
        this.userId = teamMember.getUser().getId();
        this.nickname = teamMember.getUser().getNickname();
        this.email = teamMember.getUser().getEmail();
        this.profileImage = teamMember.getUser().getProfileImageUrl();
        this.role = teamMember.getRole();
        this.status = teamMember.getStatus();
        this.joinedAt = teamMember.getJoinedAt();
    }
}
