package com.jun.crewcheckback.feed.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "feed_subjects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedSubject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private String content;

    @Column(name = "en_name")
    private String enName;

    // Use Formula to calculate count dynamically
    @Formula("(SELECT count(1) FROM feed_subject_members m WHERE m.feed_subject_id = id)")
    private Integer participantCount;

    @Column(name = "cover_image")
    private String coverImage;

    @OneToMany(mappedBy = "feedSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedSubjectMember> members = new ArrayList<>();

    @Builder
    public FeedSubject(String subjectName, String content, String enName, String coverImage) {
        this.subjectName = subjectName;
        this.content = content;
        this.enName = enName;
        this.coverImage = coverImage;
    }

    public void update(String subjectName, String content, String enName, String coverImage) {
        if (subjectName != null)
            this.subjectName = subjectName;
        if (content != null)
            this.content = content;
        if (enName != null)
            this.enName = enName;
        if (coverImage != null)
            this.coverImage = coverImage;
    }
}
