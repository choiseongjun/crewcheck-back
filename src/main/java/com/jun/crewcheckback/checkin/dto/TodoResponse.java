package com.jun.crewcheckback.checkin.dto;

import com.jun.crewcheckback.team.dto.TeamResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TodoResponse {
    private List<TeamResponse> approvedTeams;
    private List<TeamResponse> pendingTeams;
    private List<TeamResponse> rejectedTeams;
}
