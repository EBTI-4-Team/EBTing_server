package com.github.ebtingserver.domain.team.service;

import com.github.ebtingserver.common.exception.CustomException;
import com.github.ebtingserver.domain.participation.entity.Participation;
import com.github.ebtingserver.domain.participation.entity.ParticipationRole;
import com.github.ebtingserver.domain.participation.repository.ParticipationRepository;
import com.github.ebtingserver.domain.participation.repository.ParticipationRepository;
import com.github.ebtingserver.domain.team.dto.request.TeamCreateRequest;
import com.github.ebtingserver.domain.team.dto.response.TeamDetailResponse;
import com.github.ebtingserver.domain.team.dto.response.TeamMemberResponse;
import com.github.ebtingserver.domain.team.dto.response.TeamResponseDto;
import com.github.ebtingserver.domain.team.entity.Team;
import com.github.ebtingserver.domain.team.exception.TeamExceptionCode;
import com.github.ebtingserver.domain.team.repository.TeamRepository;
import com.github.ebtingserver.domain.user.entity.User;
import com.github.ebtingserver.domain.user.exception.UserExceptionCode;
import com.github.ebtingserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;

    public List<TeamResponseDto> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(TeamResponseDto::from)
                .toList();
    }

    @Transactional
    public void createTeam(Long userId, TeamCreateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.USER_NOT_FOUND));

        Team team = Team.builder()
                .teamName(request.teamName())
                .maxMember(request.maxMember())
                .teamExplain(request.teamExplain())
                .build();
        teamRepository.save(team);
    }

        Participation participation = Participation.builder()
                .user(user)
                .team(team)
                .role(ParticipationRole.ADMIN)
                .build();
        participationRepository.save(participation);

    @Transactional
    public TeamDetailResponse getTeamDetail(Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));
        List<TeamMemberResponse> members = participationRepository.findByTeam_TeamId(teamId)
                .stream()
                .map(p -> new TeamMemberResponse(
                        p.getUser().getUserId(),
                        p.getUser().getName(),
                        p.getUser().getEbti()
                ))
                .toList();

        return TeamDetailResponse.of(team, members);
    }


}
