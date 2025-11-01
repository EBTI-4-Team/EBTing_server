package com.github.ebtingserver.domain.team.dto.response;


import com.github.ebtingserver.domain.team.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "팀 응답")
public record TeamResponseDto(
        @Schema(description = "팀 ID", example = "1")
        long teamId,

        @Schema(description = "팀 이름", example = "개발팀")
        String teamName,

        @Schema(description = "최대 인원", example = "10")
        Integer maxMember,

        @Schema(description = "팀 설명", example = "백엔드 개발팀입니다")
        String teamExplain,

        @Schema(description = "팀원 목록")
        List<TeamMemberResponse> members
) {
    public static TeamResponseDto from(Team team, List<TeamMemberResponse> members) {
        return new TeamResponseDto(
                team.getTeamId(),
                team.getTeamName(),
                team.getMaxMember(),
                team.getTeamExplain(),
                members
        );
    }
}
