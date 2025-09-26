package org.example.ticketingdemo.domain.seat.dto.response;

import org.example.ticketingdemo.domain.seat.entity.Seat;

// 콘서트 정보는 콘서트 이름만
public record SeatConcertResponse(
        String title) {

    public static SeatConcertResponse from(Seat seat) {
        return new SeatConcertResponse(
                seat.getConcert().getTitle()
        );
    }
}

