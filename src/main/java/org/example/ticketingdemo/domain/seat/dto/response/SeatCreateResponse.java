package org.example.ticketingdemo.domain.seat.dto.response;

import org.example.ticketingdemo.domain.seat.entity.Seat;

import java.time.LocalDateTime;

public record SeatCreateResponse (
        String seatNumber,
        SeatUserResponse user,
        LocalDateTime createAt
){
    public static SeatCreateResponse from(Seat seat, SeatUserResponse seatUserResponse){
        return new SeatCreateResponse(
                seat.getSeatNumber(),
                seatUserResponse,
                LocalDateTime.now()
        );
    }
}
