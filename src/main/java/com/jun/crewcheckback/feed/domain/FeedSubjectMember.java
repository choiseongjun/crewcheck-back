package com.jun.crewcheckback.feed.domain;

import com.jun.crewcheckback.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feed_subject_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedSubjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_subject_id", nullable = false)
    private FeedSubject feedSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Builder
    public FeedSubjectMember(FeedSubject feedSubject, User user) {
        this.feedSubject = feedSubject;
        this.user = user;
    }
}
