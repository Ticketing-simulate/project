package org.example.ticketingdemo.domain.seat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.ticketingdemo.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SeatErrorCode implements ErrorCode {
    SEAT_NOT_FOUND_USER_ID("SEAT_001", HttpStatus.NOT_FOUND, "User를 찾을 수 없습니다."),
    SEAT_NOT_FOUND_SEAT("SEAT_002", HttpStatus.NOT_FOUND, "Seat를 찾을 수 없습니다."),
    SEAT_ALREADY_SOLD("SEAT_003", HttpStatus.BAD_REQUEST, "이미 판매된 좌석입니다."),
    SEAT_NOT_SOLD("SEAT_004", HttpStatus.BAD_REQUEST, "판매되지 않은 좌석은 취소할 수 없습니다."),
    SEAT_NOT_MATCH_USER("SEAT_005", HttpStatus.FORBIDDEN, "해당 좌석은 이 유저가 구매한 좌석이 아닙니다."),
    SEAT_NOT_FOUND_CONCERT_ID("SEAT_006", HttpStatus.NOT_FOUND, "Concert를 찾을 수 없습니다."),
    SEAT_ALREADY_PENDING("SEAT_007", HttpStatus.BAD_REQUEST, "현재 예약중인 좌석입니다."),
    SEAT_NOT_PENDING("SEAT_008", HttpStatus.BAD_REQUEST, "예약된 좌석이 아닙니다.");
    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}

