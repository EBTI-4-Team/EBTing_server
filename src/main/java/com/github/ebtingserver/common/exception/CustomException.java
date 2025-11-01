package com.github.ebting_server.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final int status;

    public CustomException(String message, int status) {
        super(message);
        this.status = status;
    }

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.status = exceptionCode.getCode();
    }
}
