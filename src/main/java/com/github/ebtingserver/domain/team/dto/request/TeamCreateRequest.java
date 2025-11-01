package com.github.ebtingserver.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "팀 생성 요청")
public record TeamCreateRequest(
        @Schema(description = "팀 이름", example = "개발팀", required = true)
        @NotBlank(message = "팀 이름은 필수입니다")
        String teamName,

        @Schema(description = "최대 인원", example = "10", required = true)
        @NotNull(message = "최대 인원은 필수입니다")
        @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다")
        Integer maxMember,

        @Schema(description = "팀 설명", example = "백엔드 개발팀입니다")
        String teamExplain
) {
}
