package com.github.ebtingserver.domain.team.controller;


import com.github.ebtingserver.common.dto.ResponseDTO;
import com.github.ebtingserver.domain.team.dto.request.TeamCreateRequest;
import com.github.ebtingserver.domain.team.dto.response.TeamResponseDto;
import com.github.ebtingserver.domain.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team", description = "팀 관리 API")
@RestController
@RequestMapping("api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @Operation(summary = "전체 팀 조회", description = "모든 팀의 목록을 조회합니다")
    @GetMapping
    public ResponseDTO<List<TeamResponseDto>> getAllTeams() {
        List<TeamResponseDto> teams = teamService.getAllTeams();
        return ResponseDTO.ok(teams);
    }

    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다")
    @PostMapping
    public ResponseDTO<Void> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        teamService.createTeam(request);
        return ResponseDTO.ok();
    }

}
