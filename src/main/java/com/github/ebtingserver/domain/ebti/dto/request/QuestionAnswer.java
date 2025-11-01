package com.github.ebtingserver.domain.ebti.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "질문 답변")
public class QuestionAnswer {

    @JsonProperty("QuestionId")
    @Schema(description = "질문 ID", example = "0")
    private Integer questionId;

    @JsonProperty("answer")
    @Schema(description = "답변 점수", example = "8")
    private Integer answer;

}
