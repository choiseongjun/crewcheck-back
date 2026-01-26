package com.jun.crewcheckback.feed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedSubjectRequest {
    private String subjectName;
    private String content;
    private String enName;
    private String coverImage;

    public FeedSubjectRequest(String subjectName, String content, String enName, String coverImage) {
        this.subjectName = subjectName;
        this.content = content;
        this.enName = enName;
        this.coverImage = coverImage;
    }
}
