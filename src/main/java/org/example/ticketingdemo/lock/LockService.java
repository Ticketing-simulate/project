package org.example.ticketingdemo.lock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRedisRepository lockRedisRepository;

    /**
     * 락을 걸고 특정 작업 실행
     */
    public <T> T executeWithLock(String key, Supplier<T> action,
                                 Duration waitTime, Duration leaseTime) {
        String lockValue = UUID.randomUUID().toString(); // 락 소유자 식별
        long deadline = System.currentTimeMillis() + waitTime.toMillis();

        try {
            while (System.currentTimeMillis() < deadline) {
                // 락 획득 시도(락을 실제로 거는 코드)
                if (lockRedisRepository.tryLock(key, lockValue, leaseTime)) {
                    try {
                        return action.get(); // 실제 핵심 로직 실행
                        // 락이 걸려있는 동안 다른 스레드는 action.get() 실행 못함
                    } finally {
                        // 내가 잡은 락일 때만 해제
                        lockRedisRepository.releaseLock(key, lockValue);
                    }
                }
                // 실패 시 잠깐 대기 후 재시도
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new RuntimeException("락 획득 실패: " + key);
    }
}
