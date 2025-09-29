package org.example.ticketingdemo.domain.payment.dto.response;

import lombok.Builder;
import org.example.ticketingdemo.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Builder
public record PaymentListResponse(
        Long id,
        String concertTitle,
        String seatNumber,
        Double totalPrice,
        LocalDateTime createAt
) {
    public static PaymentListResponse fromPayment(Payment payment){
        return PaymentListResponse.builder()
                .id(payment.getId())
                .concertTitle(payment.getSeat().getConcert().getTitle())
                .seatNumber(payment.getSeat().getSeatNumber())
                .totalPrice(payment.getTotalPrice())
                .createAt(payment.getCreatedAt())
                .build();
    }

}