package com.github.ebtingserver.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @Schema(description = "전화번호", example = "01012345678")
    @NotBlank(message = "전화번호를 입력해주세요")
    private String phonenumber;

    @Schema(description = "비밀번호", example = "password123!")
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

}
