package com.github.ebtingserver.team.dto.response;


import com.github.ebtingserver.team.entity.Team;

public record TeamResponseDto(
        long teamId,
        String teamName,
        Integer maxMember,
        String teamExplain
) {
    public static TeamResponseDto from(Team team) {
        return new TeamResponseDto(
                team.getTeamId(),
                team.getTeamName(),
                team.getMaxMember(),
                team.getTeamExplain()
        );
    }
}
