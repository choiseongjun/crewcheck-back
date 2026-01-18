package com.jun.crewcheckback.team.domain;

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
@Table(name = "team_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20)
    private String role; // 'leader', 'member'

    @Column(length = 20)
    private String status; // 'active', 'pending', 'banned'

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    private Integer streak = 0;

    @Column(name = "achievement_rate")
    private Double achievementRate = 0.0;

    @Builder
    public TeamMember(Team team, User user, String role, String status) {
        this.team = team;
        this.user = user;
        this.role = role != null ? role : "member";
        this.status = status != null ? status : "active";
    }
}
