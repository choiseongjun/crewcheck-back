package com.jun.crewcheckback.team.service;

import com.jun.crewcheckback.team.domain.Team;
import com.jun.crewcheckback.team.dto.TeamCreateRequest;
import com.jun.crewcheckback.team.dto.TeamMemberResponse;
import com.jun.crewcheckback.team.dto.TeamResponse;
import com.jun.crewcheckback.team.dto.TeamUpdateRequest;
import com.jun.crewcheckback.team.repository.TeamMemberRepository;
import com.jun.crewcheckback.team.repository.TeamRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final com.jun.crewcheckback.checkin.repository.CheckInRepository checkInRepository;

    @Transactional
    public TeamResponse createTeam(TeamCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Team team = Team.builder()
                .owner(user)
                .name(request.getName())
                .description(request.getDescription())
                .goal(request.getGoal())
                .introduce(request.getIntroduce())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .grade(request.getGrade())
                .memberLimit(request.getMemberLimit())
                .isPublic(request.getIsPublic())
                .requireApproval(request.getRequireApproval())
                .frequency(request.getFrequency())
                .durationDays(request.getDurationDays())
                .commonMission(request.getCommonMission())
                .startDate(request.getStartDate())
                .build();

        Team savedTeam = teamRepository.save(team);
        return new TeamResponse(savedTeam);
    }

    public TeamResponse getTeam(UUID teamId) {
        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        List<com.jun.crewcheckback.team.domain.TeamMember> members = teamMemberRepository.findAllByTeamId(teamId);
        int totalCheckIns = 0;
        int approvedCheckIns = 0;

        for (com.jun.crewcheckback.team.domain.TeamMember member : members) {
            List<com.jun.crewcheckback.checkin.domain.CheckIn> checkIns = checkInRepository
                    .findAllByUser(member.getUser());
            totalCheckIns += checkIns.size();
            approvedCheckIns += checkIns.stream().filter(c -> "approved".equals(c.getStatus())).count();
        }

        int rate = totalCheckIns > 0 ? (int) ((double) approvedCheckIns / totalCheckIns * 100) : 0;
        int currentMemberCount = members.size();
        return new TeamResponse(team, rate, currentMemberCount);
    }

    public Page<TeamResponse> getTeams(String category, String keyword, Pageable pageable) {
        return teamRepository.findTeams(category, keyword, pageable)
                .map(team -> {
                    // Note: This might cause N+1 problem if there are many teams.
                    // Ideally, we should use a batch query or subselect.
                    // For now, assuming reasonable page size/usage.
                    int memberCount = teamMemberRepository.findAllByTeamId(team.getId()).size();
                    return new TeamResponse(team, 0, memberCount);
                });
    }

    @Transactional
    public TeamResponse updateTeam(UUID teamId, TeamUpdateRequest request, String email) {
        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        if (!team.getOwner().getEmail().equals(email)) {
            throw new IllegalArgumentException("Only owner can update the team");
        }

        team.update(
                request.getName(),
                request.getDescription(),
                request.getGoal(),
                request.getIntroduce(),
                request.getImageUrl(),
                request.getCategory(),
                request.getGrade(),
                request.getMemberLimit(),
                request.getIsPublic(),
                request.getRequireApproval(),
                request.getFrequency(),
                request.getDurationDays(),
                request.getCommonMission(),
                request.getStartDate());

        return new TeamResponse(team);
    }

    @Transactional
    public void deleteTeam(UUID teamId, String email) {
        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        if (!team.getOwner().getEmail().equals(email)) {
            throw new IllegalArgumentException("Only owner can delete the team");
        }

        team.delete();
    }

    public List<TeamMemberResponse> getTeamMembers(UUID teamId) {
        // Verify team exists
        teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        return teamMemberRepository.findAllByTeamId(teamId).stream()
                .map(member -> {
                    List<com.jun.crewcheckback.checkin.domain.CheckIn> checkIns = checkInRepository
                            .findAllByUser(member.getUser());
                    int total = checkIns.size();
                    int approved = (int) checkIns.stream().filter(c -> "approved".equals(c.getStatus())).count();
                    int rate = total > 0 ? (int) ((double) approved / total * 100) : 0;
                    return new TeamMemberResponse(member, rate);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public com.jun.crewcheckback.team.dto.TeamMemberResponse joinTeam(UUID teamId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        if (teamMemberRepository.findByTeamAndUser(team, user).isPresent()) {
            throw new IllegalArgumentException("Already a member of the team");
        }

        String status = Boolean.TRUE.equals(team.getRequireApproval()) ? "pending" : "active";

        com.jun.crewcheckback.team.domain.TeamMember teamMember = com.jun.crewcheckback.team.domain.TeamMember.builder()
                .team(team)
                .user(user)
                .role("member")
                .status(status)
                .build();

        teamMemberRepository.save(teamMember);

        return new com.jun.crewcheckback.team.dto.TeamMemberResponse(teamMember);
    }

    @Transactional
    public void leaveTeam(UUID teamId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Team team = teamRepository.findByIdAndDeletedYn(teamId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        com.jun.crewcheckback.team.domain.TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new IllegalArgumentException("Not a member of the team"));

        if ("leader".equals(teamMember.getRole())) {
            throw new IllegalArgumentException("Leader cannot leave the team. Delete the team instead.");
        }

        teamMemberRepository.delete(teamMember);
    }
}
