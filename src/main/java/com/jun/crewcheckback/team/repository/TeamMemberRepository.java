package com.jun.crewcheckback.team.repository;

import com.jun.crewcheckback.team.domain.TeamMember;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    List<TeamMember> findAllByUserAndStatus(User user, String status);

    List<TeamMember> findAllByUserAndStatusAndTeamDeletedYn(User user, String status, String teamDeletedYn);

    List<TeamMember> findAllByUser(User user);

    List<TeamMember> findAllByTeamId(UUID teamId);

    java.util.Optional<TeamMember> findByTeamAndUser(com.jun.crewcheckback.team.domain.Team team, User user);
}
