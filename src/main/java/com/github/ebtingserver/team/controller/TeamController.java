package com.github.ebtingserver.team.controller;


import com.github.ebtingserver.team.dto.request.TeamCreateRequest;
import com.github.ebtingserver.team.dto.response.TeamResponseDto;
import com.github.ebtingserver.team.entity.Team;
import com.github.ebtingserver.team.repository.TeamRepository;
import com.github.ebtingserver.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamRepository teamRepository;
    private final TeamService teamService;

    //전체 팀 조회
    @GetMapping
    public ResponseEntity<List<TeamResponseDto>> getAllTeams() {
        List<TeamResponseDto> teams = teamService.getAllTeams();

        return ResponseEntity.ok(teams);
    }

    //팀 생성
    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(
            @RequestBody TeamCreateRequest request
            ){
        teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
