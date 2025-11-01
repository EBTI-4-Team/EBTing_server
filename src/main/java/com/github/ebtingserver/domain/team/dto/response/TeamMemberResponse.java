package com.github.ebtingserver.domain.team.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팀멤버정보")
public record TeamMemberResponse(
        @Schema(description = "사용자 ID", example = "101") Long userId,
        @Schema(description = "이름", example = "홍길동") String userName,
        @Schema(description = "EBTI", example = "INTJ") String ebti
) {

}
