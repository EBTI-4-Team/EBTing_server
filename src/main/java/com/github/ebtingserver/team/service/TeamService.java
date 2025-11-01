package com.github.ebtingserver.team.service;

import com.github.ebtingserver.team.dto.request.TeamCreateRequest;
import com.github.ebtingserver.team.dto.response.TeamResponseDto;
import com.github.ebtingserver.team.entity.Team;
import com.github.ebtingserver.team.repository.TeamRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public List<TeamResponseDto> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(TeamResponseDto::from)
                .toList();
    }

    @Transactional
    public void createTeam(TeamCreateRequest request){
        String teamName = request.teamName();
        Integer maxMember = request.maxMember();
        String teamExplain = request.teamExplain();

        Team team = Team.builder()
                .teamName(teamName)
                .maxMember(maxMember)
                .teamExplain(teamExplain)
                .build();
        teamRepository.save(team);

    }


}
