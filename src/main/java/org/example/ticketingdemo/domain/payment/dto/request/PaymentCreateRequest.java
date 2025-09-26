package org.example.ticketingdemo.domain.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentCreateRequest {

    @Min(1)
    @NotNull
    private final Long ticketId;
}
