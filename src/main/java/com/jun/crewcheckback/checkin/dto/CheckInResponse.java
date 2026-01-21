package com.jun.crewcheckback.checkin.dto;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.checkin.domain.CheckInApproval;
import com.jun.crewcheckback.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.stream.Collectors;

@Getter
public class CheckInResponse {
    private UUID id;
    private String userName;
    private UUID userId;
    private String teamName;
    private String content;
    private String imageUrl;
    private String routineTitle;
    private String status;
    private LocalDateTime timestamp;
    private String complex; // Just context matching, ignoring

    private String difficultyLevel;
    private List<ApproverInfo> approvers;

    public CheckInResponse(CheckIn checkIn) {
        this.id = checkIn.getId();
        this.userName = checkIn.getUser().getNickname();
        this.teamName = checkIn.getTeam().getName();
        this.content = checkIn.getContent();
        this.imageUrl = checkIn.getImageUrl();
        this.routineTitle = checkIn.getRoutineTitle();
        this.status = checkIn.getStatus();
        this.timestamp = checkIn.getTimestamp();
        this.difficultyLevel = checkIn.getDifficultyLevel();
        this.approvers = Collections.emptyList();
    }

    public CheckInResponse(CheckIn checkIn, List<CheckInApproval> approvals) {
        this.id = checkIn.getId();
        this.userName = checkIn.getUser().getNickname();
        this.userId = checkIn.getUser().getId();
        this.teamName = checkIn.getTeam().getName();
        this.content = checkIn.getContent();
        this.imageUrl = checkIn.getImageUrl();
        this.routineTitle = checkIn.getRoutineTitle();
        this.status = checkIn.getStatus();
        this.timestamp = checkIn.getTimestamp();
        this.difficultyLevel = checkIn.getDifficultyLevel();
        this.approvers = approvals.stream()
                .map(ApproverInfo::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class ApproverInfo {
        private UUID id;
        private UUID approvalId;
        private String nickname;
        private String profileImageUrl;

        public ApproverInfo(CheckInApproval approval) {
            this.id = approval.getApprover().getId();
            this.approvalId = approval.getId();
            this.nickname = approval.getApprover().getNickname();
            this.profileImageUrl = approval.getApprover().getProfileImageUrl();
        }
    }
}
