package com.github.ebtingserver.common.dto;

import com.github.ebtingserver.common.exception.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "응답 DTO")
public class ResponseDTO<Data> {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;

    @Schema(description = "응답 메시지", example = "success")
    private String message;

    @Schema(description = "응답 시간")
    private LocalDateTime timestamp;

    @Schema(description = "응답 데이터")
    private Data data;

    private ResponseDTO(int status, String message, LocalDateTime timestamp, Data data) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static <Data> ResponseDTO<Data> ok() {

        return ok(null);
    }

    public static <Data> ResponseDTO<Data> ok(Data data) {

        return of(200, "success", data);
    }

    public static <Data> ResponseDTO<Data> error(CustomException exception) {

        return of(exception.getStatus(), exception.getMessage());
    }

    public static <Data> ResponseDTO<Data> error(int status, String message) {

        return of(status, message, null);
    }

    public static <Data> ResponseDTO<Data> error(int status, String message, Data data) {

        return of(status, message, data);
    }

    private static <Data> ResponseDTO<Data> of(int status, String message) {

        return of(status, message, null);
    }

    private static <Data> ResponseDTO<Data> of(int status, String message, Data data) {

        return new ResponseDTO<>(status, message, LocalDateTime.now(), data);
    }
}
