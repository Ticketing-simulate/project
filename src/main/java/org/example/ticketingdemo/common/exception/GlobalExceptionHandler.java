package org.example.ticketingdemo.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.ticketingdemo.common.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== 비즈니스 예외 =====
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(GlobalException ex, HttpServletRequest req) {
        log.error("Business error: code={}, uri={}, method={}, msg={}",
                ex.getErrorCode().getCode(), req.getRequestURI(), req.getMethod(), ex.getMessage());
        return response(ex.getErrorCode());
    }



    // ===== 서버 오류 시 (최종) =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error: uri={}, method={}, msg={}", req.getRequestURI(), req.getMethod(), ex.getMessage(), ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
    }

    // 필요 시 검증과정 추가

    // ===== 공통 ResponseEntity 빌더 =====
    private ResponseEntity<ApiErrorResponse> response(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiErrorResponse.from(errorCode));
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus httpStatus, String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(ApiErrorResponse.from(httpStatus, message));
    }
}


