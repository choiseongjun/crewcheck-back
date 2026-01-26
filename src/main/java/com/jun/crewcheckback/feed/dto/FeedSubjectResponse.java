package com.jun.crewcheckback.feed.dto;

import com.jun.crewcheckback.feed.domain.FeedSubject;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class FeedSubjectResponse {
    private UUID id;
    private String subjectName;
    private String content;
    private String enName;
    private String coverImage;
    private Integer participantCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isJoined;

    public FeedSubjectResponse(FeedSubject entity) {
        this(entity, false);
    }

    public FeedSubjectResponse(FeedSubject entity, boolean isJoined) {
        this.id = entity.getId();
        this.subjectName = entity.getSubjectName();
        this.content = entity.getContent();
        this.enName = entity.getEnName();
        this.coverImage = entity.getCoverImage();
        this.participantCount = entity.getParticipantCount();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.isJoined = isJoined;
    }
}
