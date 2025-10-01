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
import org.example.ticketingdemo.lock.LockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
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

    private Concert concert;
    private Seat seat;
    private List<User> users;

    @BeforeEach
    void setUp() {
        concertRepository.deleteAll();
        userRepository.deleteAll();
        seatRepository.deleteAll();

        concert = concertRepository.save(new Concert("테스트 콘서트", "뮤지컬"));
        seat = seatRepository.save(Seat.create(concert, "A1"));

        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(userRepository.save(new User("user" + i, "user" + i + "@test.com")));
        }
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
