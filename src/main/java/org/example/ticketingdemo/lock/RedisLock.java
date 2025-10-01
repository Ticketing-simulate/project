package org.example.ticketingdemo.lock;

import java.lang.annotation.*;

/**
 * Redis 분산락을 메서드에 적용할 때 사용하는 커스텀 애너테이션
 */
@Target(ElementType.METHOD)   // 메서드에만 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지 (AOP가 읽기 위해 필요)

public @interface RedisLock {
    String key();               // 락 Key, Spring EL 가능 (#concertId 등)
    long waitTime() default 1000; // Lock 획득 대기 시간(ms)
    long leaseTime() default 3000; // Lock TTL(ms)
}
