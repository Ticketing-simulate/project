package org.example.ticketingdemo.domain.seat.dto.response;

import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import java.time.LocalDateTime;

public record SeatBuyResponse(
        String seatNumber,
        SeatUserResponse user,
        SeatConcertResponse concert,
        SeatStatus status,
        LocalDateTime updatedAt
) {
    public static SeatBuyResponse from(
            Seat seat,
            SeatUserResponse seatUserResponse,
            SeatConcertResponse seatConcertResponse) {
        return new SeatBuyResponse(
                seat.getSeatNumber(),
                seatUserResponse,
                seatConcertResponse,
                seat.getStatus(),
                seat.getUpdatedAt()
        );
    }
}
