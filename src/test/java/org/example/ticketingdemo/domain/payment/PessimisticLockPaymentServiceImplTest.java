package org.example.ticketingdemo.domain.payment;

import org.example.ticketingdemo.common.exception.GlobalException;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.payment.dto.request.PaymentCreateRequest;
import org.example.ticketingdemo.domain.payment.entity.Payment;
import org.example.ticketingdemo.domain.payment.repository.PaymentRepository;
import org.example.ticketingdemo.domain.payment.service.PessimisticLockPaymentServiceImpl;
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
public class PessimisticLockPaymentServiceImplTest {

    @InjectMocks
    private PessimisticLockPaymentServiceImpl pessimisticLockPaymentServiceImpl;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 비관적락_결제_동시성_테스트() throws InterruptedException {

        // User 객체 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 20L);

        // Concert 객체 생성
        Concert concert = new Concert();
        ReflectionTestUtils.setField(concert, "concertId", 10L);
        ReflectionTestUtils.setField(concert, "price", 25000.0);

        // 성공 스레드가 사용할 Seat (초기 PENDING) 1번 성공 후 상태가 SOLD로 변경
        Seat pendingSeat = new Seat();
        ReflectionTestUtils.setField(pendingSeat, "id", 1L);
        ReflectionTestUtils.setField(pendingSeat, "seatNumber", "S-15");
        ReflectionTestUtils.setField(pendingSeat, "status", SeatStatus.PENDING);
        ReflectionTestUtils.setField(pendingSeat, "concert", concert);
        ReflectionTestUtils.setField(pendingSeat, "user", user);

        // 실패 스레드가 사용할 Seat (이미 SOLD) 락 대기 후 조회했을 때
        Seat soldSeat = new Seat();
        ReflectionTestUtils.setField(soldSeat, "id", 1L);
        ReflectionTestUtils.setField(soldSeat, "seatNumber", "S-15");
        ReflectionTestUtils.setField(soldSeat, "status", SeatStatus.SOLD);
        ReflectionTestUtils.setField(soldSeat, "concert", concert);
        ReflectionTestUtils.setField(soldSeat, "user", user);


        PaymentCreateRequest request = new PaymentCreateRequest(
                10L,
                "S-15",
                25000.0
        );

        Payment payment = Payment.builder()
                .user(user)
                .seat(null)
                .totalPrice(25000.0)
                .build();

        ReflectionTestUtils.setField(payment, "id", 5L);
        // UserRepository: user 반환
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));

        // 첫 번째 호출: PENDING 좌석 반환 (성공)
        // 두 번째 호출 이후: SOLD 좌석 반환 (실패)
        when(seatRepository.findByConcertIdAndSeatNumberWithLock(10L, "S-15"))
                .thenReturn(Optional.of(pendingSeat))
                .thenReturn(Optional.of(soldSeat));

        // PaymentRepository: save 시 payment Mock 객체 반환
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        int testCount = 100;
        ExecutorService executorServie = Executors.newFixedThreadPool(30); // 동시에 접근하는 스레드 수
        CountDownLatch latch = new CountDownLatch(testCount);

        // 결과 카운트용
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < testCount; i++) {
            executorServie.submit(() -> {
                try {
                    // 동시 호출
                    pessimisticLockPaymentServiceImpl.createPayment(user.getId(), request);
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
