package com.github.ebtingserver.domain.user.controller;

import com.github.ebtingserver.common.dto.ResponseDTO;
import com.github.ebtingserver.domain.user.dto.UserResponseDto;
import com.github.ebtingserver.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "사용자 정보 조회",
            description = "사용자 ID로 사용자 정보를 조회합니다"
    )
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO<UserResponseDto>> getUserById(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        UserResponseDto responseDto = userService.getCurrentUser(userId);
        return ResponseEntity.ok(ResponseDTO.ok(responseDto));
    }

}
