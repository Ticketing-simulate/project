package org.example.ticketingdemo.domain.payment.dto.response;

import lombok.Builder;
import org.example.ticketingdemo.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Builder
public record PaymentFindResponse (
    Long id,
    String username,
    Long tickId,
    String concertTitle,
    LocalDateTime createAt
) {
    public static PaymentFindResponse fromPayment(Payment payment){
        return PaymentFindResponse.builder()
                .id(payment.getId())
                .username(payment.getUser().getUserName())
                .tickId(payment.getTicket().getId())
                .concertTitle(payment.getTicket().getConcertName())
                .createAt(payment.getCreatedAt())
                .build();
    }
}

