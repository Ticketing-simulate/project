package org.example.ticketingdemo;

import jakarta.transaction.Transactional;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.example.ticketingdemo.domain.search.repository.SearchRepository;
import org.example.ticketingdemo.domain.search.service.PopularService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@Transactional
//public class PopularTicketPemissionTest {
//
//    @Autowired
//    private PopularService popularService;
//
//    @Autowired
//    private SearchRepository searchRepository;
//
//    @BeforeEach
//    void setUp() {
//        Popular popular = new Popular();
//        popular.setTicketcounts(0);
//        popular.setId(1L);
//        searchRepository.saveAndFlush(popular);
//    }
//
//    @Test
//    void testPessimisticLockingWithMultipleThreads() throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        CountDownLatch latch = new CountDownLatch(1);
//
//        Future<?> firstThread = executor.submit(() -> {
//            popularService.incrementTicketCountWithPessimisticLock(1L);
//            latch.countDown();  // 첫 쓰레드 작업 완료 알림
//        });
//
//        Future<?> secondThread = executor.submit(() -> {
//            try {
//                // 첫 쓰레드가 락 잡고 있을 때 잠시 대기
//                latch.await();
//
//                // 두 번째 쓰레드가 같은 데이터에 접근 시도
//                popularService.incrementTicketCountWithPessimisticLock(1L);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        });
//
//       // firstThread.get();
//      //  secondThread.get();
//
//        Popular updated = searchRepository.findById(1L).orElseThrow();
//
//        // 락 덕분에 두번의 increment가 모두 성공해서 ticketcounts = 2가 되어야 함
//        assertEquals(2, updated.getTicketcounts());
//
//        executor.shutdown();
//    }
//}


