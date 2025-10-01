package org.example.ticketingdemo.domain.payment.dto.response;

import lombok.Builder;
import org.example.ticketingdemo.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Builder
public record PaymentFindResponse (
    Long id,
    String username,
    String concertTitle,
    String concertCategory,
    String concertDescription,
    String seatNumber,
    Double totalPrice,
    LocalDateTime createAt
) {
    public static PaymentFindResponse fromPayment(Payment payment){
        return PaymentFindResponse.builder()
                .id(payment.getId())
                .username(payment.getUser().getUserName())
                .concertTitle(payment.getSeat().getConcert().getTitle())
                .concertCategory(payment.getSeat().getConcert().getCategory().name())
                .concertDescription(payment.getSeat().getConcert().getDescription())
                .seatNumber(payment.getSeat().getSeatNumber())
                .totalPrice(payment.getSeat().getConcert().getPrice())
                .createAt(payment.getCreatedAt())
                .build();
    }
}

