package com.github.ebtingserver.domain.user.service;

import com.github.ebtingserver.common.exception.CustomException;
import com.github.ebtingserver.domain.participation.entity.Participation;
import com.github.ebtingserver.domain.participation.repository.ParticipationRepository;
import com.github.ebtingserver.domain.user.dto.UserResponseDto;
import com.github.ebtingserver.domain.user.entity.User;
import com.github.ebtingserver.domain.user.exception.UserExceptionCode;
import com.github.ebtingserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;

    public UserResponseDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.USER_NOT_FOUND));

        // 소속 팀 목록 조회
        List<Participation> participations = participationRepository.findByUser(user);
        List<UserResponseDto.TeamInfo> teams = participations.stream()
                .map(participation -> UserResponseDto.TeamInfo.builder()
                        .teamId(participation.getTeam().getTeamId())
                        .teamName(participation.getTeam().getTeamName())
                        .role(participation.getRole().name())
                        .build())
                .toList();

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .ebti(user.getEbti())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .teams(teams)
                .build();
    }

}
