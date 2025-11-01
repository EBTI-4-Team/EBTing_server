package com.github.ebtingserver.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInfoUpdate(
        @Schema(description = "팀 이름", example = "개발팀")
        String teamName,

        @Schema(description = "최대 인원", example = "10")
        Integer maxMember,

        @Schema(description = "팀 설명", example = "백엔드 개발팀입니다")
        String teamExplain
) {
}
