package org.example.ticketingdemo;


import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.example.ticketingdemo.domain.seat.dto.request.SeatBuyRequest;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.repository.SeatRepository;
import org.example.ticketingdemo.domain.seat.service.SeatInternalService;
import org.example.ticketingdemo.domain.user.entity.User;
import org.example.ticketingdemo.domain.user.repository.UserRepository;
import org.example.ticketingdemo.domain.payment.repository.PaymentRepository;
import org.example.ticketingdemo.lock.LockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class SeatInternalServiceConcurrencyTest {

    @Autowired
    private SeatInternalService seatInternalService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private LockService lockService;

    @Autowired
    private PaymentRepository paymentRepository;


    private Concert concert;
    private Seat seat;
    private List<User> users;

    @BeforeEach
    void setUp() {
        // DB 초기화하지 않고 Supabase의 기존 데이터 그대로 사용
        concert = concertRepository.findAll(PageRequest.of(0, 1))
                .getContent().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("콘서트 데이터가 존재하지 않음"));

        seat = seatRepository.findByConcertIdAndSeatNumber(concert.getConcertId(), "A1")
                .orElseThrow(() -> new RuntimeException("좌석 A1이 존재하지 않음"));

        users = userRepository.findAll(); // 이미 있는 유저 목록 가져오기
    }

    @Test
    void 동시에_여러명이_같은좌석_구매요청_시_동시성_문제_발생() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int userIndex = i;
            executor.submit(() -> {
                try {
                    barrier.await();
                    seatInternalService.buySeat(new SeatBuyRequest("A1"), users.get(userIndex).getId(), concert.getConcertId());
                } catch (Exception e) {
                    // 동시성 문제로 발생한 예외 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Seat result = seatRepository.findById(seat.getId()).orElseThrow();
        System.out.println("Seat 상태: " + result.getStatus() + ", User: " + (result.getUser() != null ? result.getUser().getUserName() : null));
    }

    @Test
    void 동시에_여러명이_같은좌석_구매요청_시_RedisLock으로_동시성_보장() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int userIndex = i;
            executor.submit(() -> {
                try {
                    lockService.executeWithLock(
                            "seat:" + seat.getId(),
                            () -> {
                                Seat s = seatRepository.findById(seat.getId()).orElse(null);
                                if (s != null && s.getStatus() == SeatStatus.AVAILABLE) {
                                    s.changeStatus(SeatStatus.SOLD);
                                    s.assignUser(users.get(userIndex));
                                    seatRepository.save(s);
                                    successCount.incrementAndGet();
                                }
                                return null;
                            },
                            Duration.ofSeconds(5),
                            Duration.ofSeconds(2)
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Redis 락 덕분에 동시에 한 명만 구매 가능
        assertThat(successCount.get()).isEqualTo(1);

        Seat result = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(SeatStatus.SOLD);
        assertThat(result.getUser()).isNotNull();

        System.out.println("동시성 보장 후 Seat 상태: " + result.getStatus() + ", User: " + result.getUser().getUserName());
    }
}
