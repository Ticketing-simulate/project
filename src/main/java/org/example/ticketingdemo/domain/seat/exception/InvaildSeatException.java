package org.example.ticketingdemo.domain.seat.exception;

import org.example.ticketingdemo.common.exception.GlobalException;

public class InvaildSeatException extends GlobalException {
    public InvaildSeatException(SeatErrorCode errorCode) {
        super(errorCode);
    }
}
