package org.example.ticketingdemo.domain.payment.service;

import jakarta.validation.Valid;
import org.example.ticketingdemo.domain.payment.dto.request.PaymentCreateRequest;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentListResponse;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentCreateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PaymentService {

    Page<PaymentListResponse> findAll(Long userId, Pageable pageable);

    PaymentCreateResponse createPayment(Long userId, @Valid PaymentCreateRequest request);
}
