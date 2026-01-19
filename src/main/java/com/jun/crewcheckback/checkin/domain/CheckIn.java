package com.jun.crewcheckback.checkin.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import com.jun.crewcheckback.team.domain.Team;
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
@Table(name = "check_ins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckIn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "routine_title")
    private String routineTitle;

    @Column(length = 20)
    private String status; // 'pending', 'approved', 'rejected'

    private String difficultyLevel; //easy,normal,hard

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    @Builder
    public CheckIn(Team team, User user, String content, String imageUrl, String routineTitle, String status,String difficultyLevel) {
        this.team = team;
        this.user = user;
        this.content = content;
        this.imageUrl = imageUrl;
        this.routineTitle = routineTitle;
        this.difficultyLevel = difficultyLevel;
        this.status = status != null ? status : "pending";
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void update(String content, String imageUrl, String routineTitle) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.routineTitle = routineTitle;
    }
}
