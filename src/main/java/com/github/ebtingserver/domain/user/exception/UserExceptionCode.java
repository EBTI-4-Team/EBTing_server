package com.github.ebtingserver.domain.user.exception;

import com.github.ebtingserver.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserExceptionCode implements ExceptionCode {

    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다"),
    UNAUTHORIZED_ACCESS(403, "본인의 정보만 조회할 수 있습니다"),
    MISSING_TOKEN(401, "인증 토큰이 필요합니다");

    private final int code;
    private final String message;

}
