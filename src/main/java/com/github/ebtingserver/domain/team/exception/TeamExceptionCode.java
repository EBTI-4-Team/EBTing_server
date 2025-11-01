package com.github.ebtingserver.domain.team.exception;

import com.github.ebtingserver.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamExceptionCode implements ExceptionCode {

    TEAM_NOT_FOUND(404, "팀을 찾을 수 없습니다"),
    DUPLICATE_TEAM_NAME(409, "이미 존재하는 팀 이름입니다"),
    INVALID_MAX_MEMBER(400, "최대 인원은 1명 이상이어야 합니다");

    private final int code;
    private final String message;

}
