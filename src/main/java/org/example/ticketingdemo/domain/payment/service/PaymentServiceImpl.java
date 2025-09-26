package org.example.ticketingdemo.domain.payment.service;


import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.common.exception.GlobalException;
import org.example.ticketingdemo.common.util.ErrorCodeEnum;
import org.example.ticketingdemo.domain.payment.dto.request.PaymentCreateRequest;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentListResponse;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentCreateResponse;
import org.example.ticketingdemo.domain.payment.entity.Payment;
import org.example.ticketingdemo.domain.payment.temporary.TicketRepository;
import org.example.ticketingdemo.domain.payment.temporary.Ticket;
import org.example.ticketingdemo.domain.payment.repository.PaymentRepository;
import org.example.ticketingdemo.domain.user.entity.User;
import org.example.ticketingdemo.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public PaymentCreateResponse createPayment(Long userId, PaymentCreateRequest request) {

        // 1. 사용자 및 임시 티켓 유효성 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.USER_NOT_FOUND));

        // 티켓 유효성 검증
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.TICKET_NOT_FOUND));

        Long totalPrice = ticket.getPrice();

        // 결제 정보 생성
        Payment payment = Payment.builder()
                .user(user)
                .ticket(ticket)
                .totalPrice(ticket.getPrice())
                .build();
        paymentRepository.save(payment);

        return PaymentCreateResponse.fromPayment(payment);
    }

    @Override
    public Page<PaymentListResponse> findAll(Long userId, Pageable pageable) {

        return paymentRepository.findByUserId(userId, pageable)
                .map(PaymentListResponse::fromPayment);
    }
}
