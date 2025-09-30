package org.example.ticketingdemo.domain.payment;

import org.example.ticketingdemo.common.exception.GlobalException;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.payment.dto.request.PaymentCreateRequest;
import org.example.ticketingdemo.domain.payment.entity.Payment;
import org.example.ticketingdemo.domain.payment.repository.PaymentRepository;
import org.example.ticketingdemo.domain.payment.service.NoLockPaymentServiceImpl;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.repository.SeatRepository;
import org.example.ticketingdemo.domain.user.entity.User;
import org.example.ticketingdemo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NoLockPaymentServiceImplTest {

    @InjectMocks
    private NoLockPaymentServiceImpl noLockPaymentServiceImpl;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 결제_동시성_테스트() throws InterruptedException {

        // User 객체 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 20L);

        // Concert 객체 생성
        Concert concert = new Concert();
        ReflectionTestUtils.setField(concert, "concertId", 10L);
        ReflectionTestUtils.setField(concert, "price", 25000.0);

        // Seat 객체 생성
        Seat seat = new Seat();
        ReflectionTestUtils.setField(seat, "id", 1L);
        ReflectionTestUtils.setField(seat, "seatNumber", "S-15");
        ReflectionTestUtils.setField(seat, "status", SeatStatus.PENDING);
        ReflectionTestUtils.setField(seat, "concert", concert);
        ReflectionTestUtils.setField(seat, "user", user);

        PaymentCreateRequest request = new PaymentCreateRequest(
                10L,
                "S-15",
                25000.0
        );

        Payment payment = Payment.builder()
                .user(user)
                .seat(seat)
                .totalPrice(25000.0)
                .build();

        ReflectionTestUtils.setField(payment, "id", 5L); //paymentId=5라고 가정

        // UserRepository: user 반환
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));

        // SeatRepository: seat 반환 (동일한 seat 객체를 모든 스레드가 공유)
        when(seatRepository.findByConcertIdAndSeatNumber(10L, "S-15"))
                .thenReturn(Optional.of(seat));

        // PaymentRepository: save 시 payment Mock 객체 반환
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        int testCount = 100;
        ExecutorService executorServie = Executors.newFixedThreadPool(30); // 경쟁 정도
        CountDownLatch latch = new CountDownLatch(testCount);

        // 결과 카운트용
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < testCount; i++) {
            executorServie.submit(() -> {
                try {
                    // 동시 호출
                    noLockPaymentServiceImpl.createPayment(user.getId(), request);
                    successCount.incrementAndGet();
                } catch (GlobalException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        // 모든 스레드가 작업 완료할 때까지 대기
        latch.await();

        // 결과 검증
        System.out.println("총 시도 횟수: " + testCount);
        System.out.println("성공 횟수 (paymentRepository.save() 호출 횟수): " + successCount.get());
        System.out.println("실패 횟수 : " + failCount.get());
    }
}
