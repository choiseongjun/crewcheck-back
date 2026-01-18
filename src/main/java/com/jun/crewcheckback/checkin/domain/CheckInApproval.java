package com.jun.crewcheckback.checkin.domain;

import com.jun.crewcheckback.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

import com.jun.crewcheckback.global.entity.BaseEntity;

@Entity
@Table(name = "check_in_approvals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckInApproval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_id", nullable = false)
    private CheckIn checkIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Builder
    public CheckInApproval(CheckIn checkIn, User approver) {
        this.checkIn = checkIn;
        this.approver = approver;
    }
}
