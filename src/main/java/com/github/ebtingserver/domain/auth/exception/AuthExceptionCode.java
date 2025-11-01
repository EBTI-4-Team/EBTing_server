package com.github.ebtingserver.domain.auth.exception;

import com.github.ebtingserver.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {

    DUPLICATE_PHONENUMBER(409, "이미 가입된 전화번호입니다"),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다"),
    INVALID_PASSWORD(401, "비밀번호가 일치하지 않습니다");

    private final int code;
    private final String message;

}
