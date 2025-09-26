package org.example.ticketingdemo.domain.seat.dto.response;

import org.example.ticketingdemo.domain.seat.entity.Seat;

import java.time.LocalDateTime;

public record SeatBuyResponse (
        String seatNumber,
        SeatUserResponse user,
        LocalDateTime createAt
){
    public static SeatBuyResponse from(Seat seat, SeatUserResponse seatUserResponse){
        return new SeatBuyResponse(
                seat.getSeatNumber(),
                seatUserResponse,
                LocalDateTime.now()
        );
    }
}