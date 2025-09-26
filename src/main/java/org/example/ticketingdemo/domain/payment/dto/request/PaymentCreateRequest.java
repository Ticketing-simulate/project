package org.example.ticketingdemo.domain.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
        @Min(1) @NotNull
        Long seatId,
        Long totalPrice //실제 가격과 맞는지 검증용
) {}
