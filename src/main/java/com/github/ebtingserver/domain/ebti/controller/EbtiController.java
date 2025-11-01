package com.github.ebtingserver.domain.ebti.controller;

import com.github.ebtingserver.common.dto.ResponseDTO;
import com.github.ebtingserver.domain.ebti.dto.request.EbtiCalculateRequest;
import com.github.ebtingserver.domain.ebti.dto.response.EbtiCalculateResponse;
import com.github.ebtingserver.domain.ebti.service.EbtiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EBTI", description = "EBTI 계산 API")
@RestController
@RequestMapping("/api/ebti")
@RequiredArgsConstructor
public class EbtiController {

    private final EbtiService ebtiService;

    @Operation(
            summary = "EBTI 계산",
            description = "20개의 질문 답변을 받아 EBTI 타입을 계산하고 사용자에게 저장합니다"
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<EbtiCalculateResponse>> calculateEbti(
            @io.swagger.v3.oas.annotations.Parameter(description = "사용자 ID", required = true)
            @org.springframework.web.bind.annotation.RequestParam Long userId,
            @Valid @RequestBody EbtiCalculateRequest request) {

        EbtiCalculateResponse response = ebtiService.calculateEbti(userId, request);
        return ResponseEntity.ok(ResponseDTO.ok(response));
    }

}
