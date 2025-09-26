package org.example.ticketingdemo.domain.seat.dto.response;

import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;

import java.time.LocalDateTime;

public record SeatCancelResponse(
        String seatNumber,
        SeatConcertResponse concert,
        SeatStatus status,
        LocalDateTime updatedAt
) {
    public static SeatCancelResponse from(
            Seat seat,
            SeatConcertResponse seatConcertResponse
    ) {
        return new SeatCancelResponse(
                seat.getSeatNumber(),
                seatConcertResponse,
                seat.getStatus(),
                seat.getUpdatedAt()
        );
    }
}
