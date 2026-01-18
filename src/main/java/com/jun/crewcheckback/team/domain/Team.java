package com.jun.crewcheckback.team.domain;

import com.jun.crewcheckback.global.entity.BaseEntity;
import com.jun.crewcheckback.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 1)
    private String grade; // 'S', 'A', 'B', 'C', 'D', 'F'

    @Column(name = "member_limit")
    private Integer memberLimit;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "require_approval")
    private Boolean requireApproval;

    @Column(nullable = false, length = 20)
    private String frequency; // 'daily', 'weekly', 'free'

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "common_mission")
    private String commonMission;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Builder
    public Team(User owner, String name, String description, String imageUrl, String category, String grade,
            Integer memberLimit, Boolean isPublic, Boolean requireApproval, String frequency, Integer durationDays,
            String commonMission, LocalDate startDate) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.grade = grade != null ? grade : "C";
        this.memberLimit = memberLimit != null ? memberLimit : 10;
        this.isPublic = isPublic != null ? isPublic : true;
        this.requireApproval = requireApproval != null ? requireApproval : false;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.commonMission = commonMission;
        this.startDate = startDate;
    }

    public void update(String name, String description, String imageUrl, String category, String grade,
            Integer memberLimit, Boolean isPublic, Boolean requireApproval, String frequency,
            Integer durationDays, String commonMission, LocalDate startDate) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.grade = grade;
        this.memberLimit = memberLimit;
        this.isPublic = isPublic;
        this.requireApproval = requireApproval;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.commonMission = commonMission;
        this.startDate = startDate;
    }
}
