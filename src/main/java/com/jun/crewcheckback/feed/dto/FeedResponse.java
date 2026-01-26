package com.jun.crewcheckback.feed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jun.crewcheckback.feed.domain.Feed;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class FeedResponse {
    private UUID id;
    private String userId;
    private String userName;
    private String userAvatar;
    private String topicId;
    private String content;
    private String imageUrl;
    private String mood;
    private LocalDateTime createdAt;
    private int likeCount;
    private int commentCount;

    private boolean liked;

    @JsonProperty("isLiked")
    public boolean isLiked() {
        return liked;
    }

    public FeedResponse(Feed feed, boolean isLiked) {
        this.id = feed.getId();
        this.userId = feed.getUser().getId().toString();
        this.userName = feed.getUser().getNickname();
        this.userAvatar = feed.getUser().getProfileImageUrl();
        this.topicId = feed.getFeedSubject().getId().toString();
        this.content = feed.getContent();
        this.imageUrl = feed.getImageUrl();
        this.mood = feed.getMood();
        this.createdAt = feed.getCreatedAt();
        this.likeCount = feed.getLikeCount();
        this.commentCount = feed.getCommentCount();
        this.liked = isLiked;
    }
}
