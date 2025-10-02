package org.example.ticketingdemo;

import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.concert.enums.Category;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SeatInternalServiceConcurrencyTest {

    @Autowired
    private SeatInternalService seatInternalService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ConcertRepository concertRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Seat seat;
    private List<User> users;
    private Concert concert;

    @BeforeEach
    void setUp() {
        // User 생성
        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User("bigfan" + i, "bigfan" + i + "@test.com", "password123!");
            user = userRepository.save(user);
            users.add(user);
        }


        // Concert 생성
        concert = new Concert();
        concert.setTitle("RedisLock Test Concert");
        concert.setCategory(Category.MUSICAL);
        concert.setPrice(50000.0);
        concert.setSeat(10);       // not null 유지
        concert.setTicket(0);
        concert.setDescription("동시성 테스트용 콘서트");
        concert.setCreatedAt(LocalDateTime.now());
        concert.setModifiedAt(LocalDateTime.now());
        concert = concertRepository.saveAndFlush(concert);

        // Seat 생성 (ReflectionTestUtils 사용)
        seat = new Seat();
        ReflectionTestUtils.setField(seat, "seatNumber", "A1");
        ReflectionTestUtils.setField(seat, "status", SeatStatus.AVAILABLE);
        ReflectionTestUtils.setField(seat, "concert", concert);
        seat = seatRepository.save(seat);
    }

    @Test
    void 동시에_여러명이_같은좌석_구매요청_시_RedisLock으로_동시성_보장() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    // 트랜잭션을 각 스레드에서 분리
                    TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
                    txTemplate.execute(status -> {
                        try {
                            seatInternalService.buySeatWithRedisLock(
                                    new SeatBuyRequest(seat.getSeatNumber()),
                                    users.get(idx).getId(),
                                    concert.getConcertId()
                            );
                        } catch (Exception e) {
                            // 이미 다른 스레드가 구매했으면 예외 발생 가능
                        }
                        return null;
                    });
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // DB에 실제로 반영된 최신 상태를 조회
        Seat updatedSeat = seatRepository.findById(seat.getId())
                .orElseThrow();

        assertEquals(SeatStatus.PENDING, updatedSeat.getStatus()); // 한 스레드만 좌석을 구매(PENDING 상태로 변경) 했는지 확인
        assertNotNull(updatedSeat.getUser()); // 좌석이 할당된 유저 확인
    }
}