package com.github.ebting_server.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class RequestDTO<Data> {

    LocalDateTime timestamp;
    Data data;
}
