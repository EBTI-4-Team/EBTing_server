package com.github.ebtingserver.domain.ebti.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "EBTI 계산 요청 DTO", example = """
    {
      "data": {
        "question": [
          { "QuestionId": 0,  "answer": 8 },
          { "QuestionId": 1,  "answer": 7 },
          { "QuestionId": 2,  "answer": 9 },
          { "QuestionId": 3,  "answer": 6 },
          { "QuestionId": 4,  "answer": 7 },
          { "QuestionId": 5,  "answer": 5 },
          { "QuestionId": 6,  "answer": 8 },
          { "QuestionId": 7,  "answer": 9 },
          { "QuestionId": 8,  "answer": 7 },
          { "QuestionId": 9,  "answer": 8 },
          { "QuestionId": 10, "answer": 9 },
          { "QuestionId": 11, "answer": 8 },
          { "QuestionId": 12, "answer": 7 },
          { "QuestionId": 13, "answer": 9 },
          { "QuestionId": 14, "answer": 8 },
          { "QuestionId": 15, "answer": 7 },
          { "QuestionId": 16, "answer": 8 },
          { "QuestionId": 17, "answer": 9 },
          { "QuestionId": 18, "answer": 9 },
          { "QuestionId": 19, "answer": 10 }
        ]
      }
    }
    """)
public class EbtiCalculateRequest {

    @NotNull
    @Schema(description = "질문 데이터")
    private Data data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Data {

        @NotNull
        @Size(min = 20, max = 20)
        @JsonProperty("question")
        @Schema(description = "질문 답변 리스트 (20개)")
        private List<QuestionAnswer> question;
    }

}
