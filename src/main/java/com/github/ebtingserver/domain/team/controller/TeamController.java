package com.github.ebtingserver.domain.team.controller;


import com.github.ebtingserver.common.dto.ResponseDTO;
import com.github.ebtingserver.domain.team.dto.request.TeamCreateRequest;
import com.github.ebtingserver.domain.team.dto.request.TeamInfoUpdate;
import com.github.ebtingserver.domain.team.dto.response.TeamDetailResponse;
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
        teamService.createTeam(request.userId(), request);
        return ResponseDTO.ok();
    }

    @Operation(summary = "팀 상세 조회", description = "팀 정보를 상세 조회 합니다. ")
    @GetMapping("/{teamId}")
    public ResponseDTO<TeamDetailResponse> getTeamDetail(@PathVariable Long teamId) {
        return ResponseDTO.ok(teamService.getTeamDetail(teamId));
    }

    @Operation(summary = "팀 정보 수정", description = "팀의 이름, 최대 인원, 설명을 수정합니다")
    @PatchMapping("/{teamId}")
    public ResponseDTO<Void> updateTeamInfo(@PathVariable Long teamId, @RequestBody TeamInfoUpdate request) {
        teamService.updateTeamInfo(teamId, request);
        return ResponseDTO.ok();
    }

    @Operation(summary = "팀 삭제", description = "팀을 삭제합니다 (ADMIN 권한 필요)")
    @DeleteMapping("/{teamId}")
    public ResponseDTO<Void> deleteTeam(@PathVariable Long teamId, @RequestParam Long userId) {
        teamService.deleteTeam(teamId, userId);
        return ResponseDTO.ok();
    }


}
