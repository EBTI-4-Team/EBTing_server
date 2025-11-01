package com.github.ebtingserver.domain.user.dto;

import com.github.ebtingserver.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "EBTI 성격 유형", example = "ISTJ")
    private String ebti;

    @Schema(description = "생성일시", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-01T12:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "소속 팀 목록")
    private List<TeamInfo> teams;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .ebti(user.getEbti())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "팀 정보")
    public static class TeamInfo {
        @Schema(description = "팀 ID", example = "1")
        private Long teamId;

        @Schema(description = "팀 이름", example = "개발팀")
        private String teamName;

        @Schema(description = "역할", example = "ADMIN")
        private String role;
    }

}
