package org.example.ticketingdemo.domain.payment.dto.response;

import lombok.Builder;
import org.example.ticketingdemo.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Builder
public record PaymentCreateResponse (
    Long id,
    String concertTitle,
    Double totalPrice,
    LocalDateTime createAt
) {
    public static PaymentCreateResponse fromPayment(Payment payment){
        return PaymentCreateResponse.builder()
                .id(payment.getId())
                .concertTitle(payment.getSeat().getConcert().getTitle())
                .totalPrice(payment.getTotalPrice())
                .createAt(payment.getCreatedAt())
                .build();
    }
}
