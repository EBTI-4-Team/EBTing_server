package com.github.ebtingserver.domain.ebti;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ebtingserver.domain.ebti.dto.request.EbtiCalculateRequest;
import com.github.ebtingserver.domain.ebti.dto.request.QuestionAnswer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class EbtiJsonTest {

    @Test
    public void testJsonSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // QuestionAnswer 리스트 생성
        List<QuestionAnswer> questionList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            questionList.add(QuestionAnswer.builder()
                    .questionId(i)
                    .answer(7 + (i % 4))
                    .build());
        }

        // Data 객체 생성
        EbtiCalculateRequest.Data data = EbtiCalculateRequest.Data.builder()
                .question(questionList)
                .build();

        // EbtiCalculateRequest 객체 생성
        EbtiCalculateRequest request = EbtiCalculateRequest.builder()
                .data(data)
                .build();

        // JSON으로 변환
        String json = objectMapper.writeValueAsString(request);

        System.out.println("=".repeat(80));
        System.out.println("Generated JSON:");
        System.out.println("=".repeat(80));
        System.out.println(json);
        System.out.println("=".repeat(80));

        // Pretty print
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
        System.out.println("\nPretty JSON:");
        System.out.println("=".repeat(80));
        System.out.println(prettyJson);
        System.out.println("=".repeat(80));
    }
}
