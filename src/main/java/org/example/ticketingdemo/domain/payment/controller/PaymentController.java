package org.example.ticketingdemo.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.common.dto.response.ApiPageResponse;
import org.example.ticketingdemo.common.dto.response.ApiResponse;
import org.example.ticketingdemo.domain.payment.dto.request.PaymentCreateRequest;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentFindResponse;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentListResponse;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentCreateResponse;
import org.example.ticketingdemo.domain.payment.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentCreateResponse> createPayment(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody PaymentCreateRequest request
    ) {
        Long userId = Long.parseLong(principal.getUsername());
        PaymentCreateResponse payments = paymentService.createPayment(userId, request);

        return ApiResponse.ofSuccess("결제가 성공적으로 완료되었습니다.", payments);
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiPageResponse<PaymentListResponse>> findAll(
            @AuthenticationPrincipal User principal,
            @PageableDefault Pageable pageable
    ) {
        Long userId = Long.parseLong(principal.getUsername());
        Page<PaymentListResponse> payments = paymentService.findAll(userId, pageable);

        return ApiPageResponse.success(payments);
    }

    @GetMapping("/payments/{paymentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentFindResponse> findPayment(
            @AuthenticationPrincipal User principal,
            @PathVariable long paymentId
    ) {
        Long userId = Long.parseLong(principal.getUsername());
        PaymentFindResponse payments = paymentService.find(userId, paymentId);

        return ApiResponse.ofSuccess("결제 내역 조회했습니다.", payments);
    }

}
