package org.example.ticketingdemo.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getCode();
    HttpStatus getHttpStatus();
    String getMessage();
}


//// 구현 예시
//@Getter
//@RequiredArgsConstructor
//public enum SomethingErrorCode implements ErrorCode {
//
//    INVALID_USER(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다.");
//
//    private final HttpStatus httpStatus;
//    private final String message;
//}