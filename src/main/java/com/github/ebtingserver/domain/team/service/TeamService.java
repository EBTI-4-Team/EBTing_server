package com.github.ebtingserver.domain.team.service;

import com.github.ebtingserver.common.exception.CustomException;
import com.github.ebtingserver.domain.participation.entity.Participation;
import com.github.ebtingserver.domain.participation.entity.ParticipationRole;
import com.github.ebtingserver.domain.participation.repository.ParticipationRepository;
import com.github.ebtingserver.domain.team.dto.request.TeamCreateRequest;
import com.github.ebtingserver.domain.team.dto.request.TeamInfoUpdate;
import com.github.ebtingserver.domain.team.dto.response.TeamDetailResponse;
import com.github.ebtingserver.domain.team.dto.response.TeamMemberResponse;
import com.github.ebtingserver.domain.team.dto.response.TeamResponseDto;
import com.github.ebtingserver.domain.team.entity.Team;
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

        Participation participation = Participation.builder()
                .user(user)
                .team(team)
                .role(ParticipationRole.ADMIN)
                .build();
        participationRepository.save(participation);
    }

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

    @Transactional
    public void updateTeamInfo(Long teamId, TeamInfoUpdate request){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));
        team.update(request.teamName(), request.maxMember(), request.teamExplain());
    }

    @Transactional
    public void deleteTeam(Long teamId, Long userId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));
        boolean isAdmin = participationRepository
                .existsByTeam_TeamIdAndUser_UserIdAndRole(teamId, userId, ParticipationRole.ADMIN);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다 (ADMIN 전용)");
        }

        // 팀원 확인 - MEMBER가 있으면 삭제 불가
        List<Participation> participations = participationRepository.findByTeam_TeamId(teamId);
        boolean hasMember = participations.stream()
                .anyMatch(p -> p.getRole() == ParticipationRole.MEMBER);

        if (hasMember) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "팀원이 있어 삭제할 수 없습니다. 먼저 모든 팀원을 내보내주세요");
        }

        // ADMIN만 있을 경우 삭제 진행
        participationRepository.deleteAll(participations);
        teamRepository.deleteById(teamId);
    }


}
