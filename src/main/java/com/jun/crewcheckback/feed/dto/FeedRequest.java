package com.jun.crewcheckback.feed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedRequest {
    private String feedSubjectId;
    private String content;
    private String imageUrl;
    private String mood;
}
