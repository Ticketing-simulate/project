package org.example.ticketingdemo.lock;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect // 해당 클래스가 AOP(Aspect-Oriented Programming)용 Aspect임을 나타냄
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final LockService lockService; // 실제 Redis 기반 Lock을 수행하는 서비스

    // @RedisLock 어노테이션이 붙은 메서드에 대해 AOP를 적용
    // 즉, 특정 메서드가 실행될 때 자동으로 공통 기능을 끼워 넣는단느 뜻. 여기서는 자동으로 Redis Lock 처리 기능을 끼워 넣음.
    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        // RedisLock 어노테이션에서 설정한 key, waitTime, leaseTime을 가져옴
        String key = redisLock.key();
        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();

        // LockService의 executeWithLock 호출
        //     - key: Lock을 걸 Redis key
        //     - 람다: 실제 메서드 실행 (joinPoint.proceed())
        //     - waitTime: Lock을 얻기 위해 기다릴 최대 시간
        //     - leaseTime: Lock 유지 시간 (자동 해제 시간)
        return lockService.executeWithLock(
                key,
                () -> {
                    try {
                        // 실제 비즈니스 로직 메서드 실행
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        // joinPoint.proceed()에서 예외 발생 시 RuntimeException으로 래핑
                        throw new RuntimeException(e);
                    }
                },
                Duration.ofMillis(waitTime),
                Duration.ofMillis(leaseTime)
        );
    }
}
