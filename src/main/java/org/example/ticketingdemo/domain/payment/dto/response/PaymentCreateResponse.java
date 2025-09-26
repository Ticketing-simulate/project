package org.example.ticketingdemo.domain.payment.dto.response;

import lombok.Builder;
import org.example.ticketingdemo.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Builder
public record PaymentCreateResponse (
    Long id,
    Long userId,
    Double totalPrice,
    LocalDateTime createAt
) {
    public static PaymentCreateResponse fromPayment(Payment payment){
        return PaymentCreateResponse.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .totalPrice(payment.getTotalPrice())
                .createAt(payment.getCreatedAt())
                .build();
    }
}
