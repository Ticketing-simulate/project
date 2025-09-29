package org.example.ticketingdemo.domain.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
        @NotNull
        Long concertId,
        @NotNull
        String seatNumber,
        @NotNull
        Double totalPrice //실제 가격과 맞는지 검증용
) {}
