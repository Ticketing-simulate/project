package org.example.ticketingdemo.domain.payment.dto.response;

import lombok.Builder;
import org.example.ticketingdemo.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Builder
public record PaymentCancelResponse (
    Long id,
    LocalDateTime deleteAt
) {
    public static PaymentCancelResponse fromPayment(Payment payment){
        return PaymentCancelResponse.builder()
                .id(payment.getId())
                .deleteAt(payment.getDeletedAt())
                .build();
    }
}
