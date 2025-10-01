package org.example.ticketingdemo.domain.payment;

import org.example.ticketingdemo.common.exception.GlobalException;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.concert.enums.Category;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.example.ticketingdemo.domain.payment.dto.request.PaymentCreateRequest;
import org.example.ticketingdemo.domain.payment.repository.PaymentRepository;
import org.example.ticketingdemo.domain.payment.service.NoLockPaymentServiceImpl;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.repository.SeatRepository;
import org.example.ticketingdemo.domain.user.entity.User;
import org.example.ticketingdemo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


// Spring Boot 통합 테스트
@SpringBootTest
@ImportAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:5432/postgres",
        "spring.datasource.username=postgres.mxttgcloaselbfnympov",
        "spring.datasource.password=1Y4GJn1vIDkN1YJF",
        "jwt.secret.key=xt/S0ffqB5ldIkAWtJrB1PfcuBf17La0xPNN/l6oWIQXSOYHeOxMYXMgJEq+vlIytCnImotMBDKyHThFL8jVQg"
})
public class NoLockDBconnectPaymentServiceImplTest {

    @Autowired
    private NoLockPaymentServiceImpl noLockPaymentServiceImpl;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConcertRepository concertRepository;

    @Test
    public void 결제_동시성_테스트_DB연동() throws InterruptedException {

        String uniqueEmail = "test_" + System.currentTimeMillis() + "@test.com";
        String userName = "testName_" + System.currentTimeMillis();

        // User 객체 생성
        User user = new User(userName, uniqueEmail,"test1234!");
        user = userRepository.save(user);
        final User finalUser = user;

        // Concert 객체 생성
        Concert concert = new Concert();
        concert.setTitle("testTitle");
        concert.setCategory(Category.HIP_HOP);
        concert.setPrice(25000.0);
        concert.setSeat(3);
        concert.setCreatedAt(LocalDateTime.now());
        concert.setModifiedAt(LocalDateTime.now());
        concert.setTicket(1);
        concert = concertRepository.save(concert);

        // Seat 객체 생성
        Seat seat = new Seat();
        ReflectionTestUtils.setField(seat, "seatNumber", "S-3");
        ReflectionTestUtils.setField(seat, "status", SeatStatus.PENDING);
        ReflectionTestUtils.setField(seat, "concert", concert);
        ReflectionTestUtils.setField(seat, "user", user);
        seat = seatRepository.save(seat);

        PaymentCreateRequest request = new PaymentCreateRequest(
                concert.getConcertId(),
                seat.getSeatNumber(),
                concert.getPrice()
        );

        int testCount = 500;
        ExecutorService executorServie = Executors.newFixedThreadPool(30); // 동시에 접근하는 스레드 수
        CountDownLatch latch = new CountDownLatch(testCount);

        // 결과 카운트용
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < testCount; i++) {
            executorServie.submit(() -> {
                try {
                    // 동시 호출
                    noLockPaymentServiceImpl.createPayment(finalUser.getId(), request);
                    successCount.incrementAndGet();
                } catch (GlobalException e) {
                    failCount.incrementAndGet();
                } catch (Exception e){
                    // 예상치 못한 예외도 포
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
