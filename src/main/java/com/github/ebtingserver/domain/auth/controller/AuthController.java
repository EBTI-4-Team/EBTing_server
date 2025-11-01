package com.github.ebtingserver.domain.auth.controller;

import com.github.ebtingserver.common.dto.ResponseDTO;
import com.github.ebtingserver.domain.auth.dto.LoginRequestDto;
import com.github.ebtingserver.domain.auth.dto.LoginResponseDto;
import com.github.ebtingserver.domain.auth.dto.SignupRequestDto;
import com.github.ebtingserver.domain.auth.service.AuthService;
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

@Tag(name = "인증", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = ResponseDTO.class))
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<Void>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @Operation(
            summary = "로그인",
            description = "로그인하여 JWT 토큰을 발급받습니다",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = authService.login(requestDto);
        return ResponseEntity.ok(ResponseDTO.ok(responseDto));
    }

}
