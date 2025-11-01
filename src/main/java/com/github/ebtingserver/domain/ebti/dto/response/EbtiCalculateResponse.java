package com.github.ebtingserver.domain.ebti.dto.response;

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
@Schema(description = "EBTI 계산 응답 DTO")
public class EbtiCalculateResponse {

    @JsonProperty("pred")
    @Schema(description = "예측된 EBTI 타입", example = "DICE")
    private String pred;

    @JsonProperty("pred_prob")
    @Schema(description = "예측 확률", example = "0.73")
    private Double predProb;

}
