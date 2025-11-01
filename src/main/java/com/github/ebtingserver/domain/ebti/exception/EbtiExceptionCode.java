package com.github.ebtingserver.domain.ebti.exception;

import com.github.ebtingserver.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EbtiExceptionCode implements ExceptionCode {
    FASTAPI_CONNECTION_FAILED(500, "FastAPI 서버 연결에 실패했습니다"),
    FASTAPI_REQUEST_FAILED(500, "FastAPI 요청 처리 중 오류가 발생했습니다"),
    INVALID_RESPONSE(500, "FastAPI 응답을 처리할 수 없습니다");

    private final int code;
    private final String message;
}
