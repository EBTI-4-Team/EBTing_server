package com.github.ebtingserver.domain.team.dto.response;

import com.github.ebtingserver.domain.team.entity.Report;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "팀 리포트 응답")
public record TeamReportResponse(
        @Schema(description = "리포트 ID")
        Long reportId,

        @Schema(description = "팀 ID")
        Long teamId,

        @Schema(description = "팀 이름")
        String teamName,

        @Schema(description = "팀원 수")
        int memberCount,

        @Schema(description = "GPT가 생성한 리포트 내용")
        String report,

        @Schema(description = "생성 일시")
        LocalDateTime createdAt,

        @Schema(description = "수정 일시")
        LocalDateTime updatedAt
) {
    public static TeamReportResponse from(Report report, int memberCount) {
        return TeamReportResponse.builder()
                .reportId(report.getReportId())
                .teamId(report.getTeam().getTeamId())
                .teamName(report.getTeam().getTeamName())
                .memberCount(memberCount)
                .report(report.getReportContent())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
