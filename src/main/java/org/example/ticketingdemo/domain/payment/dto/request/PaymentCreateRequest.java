package org.example.ticketingdemo.domain.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(@Min(1) @NotNull Long ticketId) {

}
