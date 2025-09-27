package org.example.ticketingdemo.domain.payment.service;


import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.common.exception.GlobalException;
import org.example.ticketingdemo.common.util.ErrorCodeEnum;
import org.example.ticketingdemo.domain.payment.dto.request.PaymentCreateRequest;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentCancelResponse;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentFindResponse;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentListResponse;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentCreateResponse;
import org.example.ticketingdemo.domain.payment.entity.Payment;
import org.example.ticketingdemo.domain.payment.repository.PaymentRepository;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.repository.SeatRepository;
import org.example.ticketingdemo.domain.seat.service.SeatExternalService;
import org.example.ticketingdemo.domain.user.entity.User;
import org.example.ticketingdemo.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final SeatExternalService ticketExternalService;

    @Override
    @Transactional
    public PaymentCreateResponse createPayment(Long userId, PaymentCreateRequest request) {

        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.USER_NOT_FOUND));

        // 좌석 유효성 검증
        Seat seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SEAT_NOT_FOUND));

        //PENDING 상태가 맞는지 확인 (결제는 PENDING된 좌석에 대해서만 가능)
        if (seat.getStatus() != SeatStatus.PENDING) {
            throw new GlobalException(ErrorCodeEnum.SEAT_NOT_FOUND);
        }

        // 요청으로 받은 금액
        BigDecimal requestedPrice = BigDecimal.valueOf(request.totalPrice());
        // DB에서 조회한 금액
        BigDecimal serverPrice = BigDecimal.valueOf(seat.getConcert().getPrice());

        // 왼쪽값이 우측보다 클 경우 1, 같을 경우 0, 작을 경우 -1 반환
        if (requestedPrice.compareTo(serverPrice) != 0){
            throw new GlobalException(ErrorCodeEnum.INVALID_PAYMENT_PRICE);
        }

        // 결제 정보 생성
        Payment payment = Payment.builder()
                .user(user)
                .seat(seat)
                .totalPrice(serverPrice.doubleValue())
                .build();
        paymentRepository.save(payment);

        // 3. 좌석 상태를 SOLD로 변경
        seat.changeStatus(SeatStatus.SOLD);
        return PaymentCreateResponse.fromPayment(payment);
    }

    @Override
    public Page<PaymentListResponse> findAll(Long userId, Pageable pageable) {

        return paymentRepository.findByUserId(userId, pageable)
                .map(PaymentListResponse::fromPayment);
    }

    @Override
    public PaymentFindResponse find(Long userId, Long paymentId) {

        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PAYMENT_NOT_FOUND));

        return PaymentFindResponse.fromPayment(payment);

    }

    @Override
    public PaymentCancelResponse delete(Long userId, Long paymentId) {

        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PAYMENT_NOT_FOUND));

        Seat seat = payment.getSeat();

        // 좌석 상태가 SOLD인지 확인
        if (seat.getStatus() != SeatStatus.SOLD) {
            throw new GlobalException(ErrorCodeEnum.SEAT_NOT_FOUND);
        }
        // 좌석 상태를 AVAILABLE로 변경
        seat.changeStatus(SeatStatus.AVAILABLE);
        // 결제 삭제
        paymentRepository.delete(payment);

        return PaymentCancelResponse.fromPayment(payment);
    }
}
