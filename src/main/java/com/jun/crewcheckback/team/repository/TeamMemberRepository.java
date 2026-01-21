package com.jun.crewcheckback.team.repository;

import com.jun.crewcheckback.team.domain.TeamMember;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    List<TeamMember> findAllByUserAndStatus(User user, String status);

    @Query("SELECT tm FROM TeamMember tm JOIN tm.team t WHERE tm.user = :user AND tm.status = :status AND t.deletedYn = :teamDeletedYn")
    List<TeamMember> findAllByUserAndStatusAndTeamDeletedYn(
            @Param("user") User user,
            @Param("status") String status,
            @Param("teamDeletedYn") String teamDeletedYn);

    List<TeamMember> findAllByUser(User user);

    List<TeamMember> findAllByTeamId(UUID teamId);

    java.util.Optional<TeamMember> findByTeamAndUser(com.jun.crewcheckback.team.domain.Team team, User user);
}
