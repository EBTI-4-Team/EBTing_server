package com.github.ebting_server.common.dto;

import com.github.ebting_server.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class ResponseDTO<Data> {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Data data;

    public static <Data> ResponseDTO<Data> ok() {

        return ok(null);
    }

    public static <Data> ResponseDTO<Data> ok(Data data) {

        return of(200, "success", data);
    }

    public static <Data> ResponseDTO<Data> error(CustomException exception) {

        return of(exception.getStatus(), exception.getMessage());
    }

    private static <Data> ResponseDTO<Data> of(int status, String message) {

        return of(status, message, null);
    }

    private static <Data> ResponseDTO<Data> of(int status, String message, Data data) {

        return new ResponseDTO<>(status, message, LocalDateTime.now(), data);
    }
}
