package org.example.ticketingdemo.common.dto.response;


import org.example.ticketingdemo.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorResponse(boolean success, String message, Object data, LocalDateTime timestamp) {

    public static ApiErrorResponse from(HttpStatus httpStatus, String message) {
        return new ApiErrorResponse(false, message, null, LocalDateTime.now());
    }

    public static ApiErrorResponse from(ErrorCode errorCode) {
        return new ApiErrorResponse(false, errorCode.getMessage(), null, LocalDateTime.now());
    }
}