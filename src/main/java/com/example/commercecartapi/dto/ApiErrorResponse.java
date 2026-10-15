package com.example.commercecartapi.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ApiErrorResponse {
    private final LocalDateTime localDateTime;
    private final int httpStatusValue;
    private final String message;
    private final String path;
}

