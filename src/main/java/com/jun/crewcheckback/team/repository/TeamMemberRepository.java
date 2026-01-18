package com.jun.crewcheckback.team.repository;

import com.jun.crewcheckback.team.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
}
