package com.jun.crewcheckback.feed.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import com.jun.crewcheckback.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.util.UUID;

@Entity
@Table(name = "feeds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_subject_id", nullable = false)
    private FeedSubject feedSubject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    private String mood;

    // Likes count
    @Formula("(SELECT count(1) FROM feed_likes fl WHERE fl.feed_id = id)")
    private int likeCount;

    // We can add Comment domain later, for now just placeholder or 0
    @Formula("0")
    private int commentCount;

    @Column(name = "deleted_yn", length = 1)
    private String deletedYn = "N";

    public void delete() {
        this.deletedYn = "Y";
    }

    public void update(FeedSubject feedSubject, String content, String imageUrl, String mood) {
        this.feedSubject = feedSubject;
        this.content = content;
        this.imageUrl = imageUrl;
        this.mood = mood;
    }

    @Builder
    public Feed(User user, FeedSubject feedSubject, String content, String imageUrl, String mood) {
        this.user = user;
        this.feedSubject = feedSubject;
        this.content = content;
        this.imageUrl = imageUrl;
        this.mood = mood;
    }
}
