package org.example.ticketingdemo.domain.seat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SeatCancelRequest(
        @NotBlank String seatNumber){

}
