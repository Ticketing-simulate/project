package org.example.ticketingdemo.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;

@Repository
public class LockRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> releaseScript;

    // Lua 스크립트: 내가 잡은 락일 때만 해제
    private static final String RELEASE_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";

    public LockRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.releaseScript = new DefaultRedisScript<>();
        this.releaseScript.setScriptText(RELEASE_LUA);
        this.releaseScript.setResultType(Long.class);
    }

    /**
     * Redis 락 획득 과정
     * @param key   - 락 키
     * @param value - 클라이언트 식별값(UUID 등)
     * @param ttl   - 락 유지 시간
     * @return true면 락 획득 성공
     */
    public boolean tryLock(String key, String value, Duration ttl) { // Redis에 key가 없으면 value를 설정하고, ttl만큼 유지
        return Boolean.TRUE.equals(redisTemplate.opsForValue() // Redis에서 반환된 Boolean을 체크
                .setIfAbsent(key, value, ttl));
    }

    /**
     * 락 해제 (내가 잡은 락일 때만 삭제)
     */
    public boolean releaseLock(String key, String value) {
        Long result = redisTemplate.execute(releaseScript, Collections.singletonList(key), value);
        return result != null && result > 0;
    }
}
