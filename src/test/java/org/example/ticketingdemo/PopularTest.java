package org.example.ticketingdemo;

import org.example.ticketingdemo.domain.search.service.PopularService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class PopularTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;


    @InjectMocks
    private PopularService popularService;

    @Test
    void getRanks_shouldReturnTop5Ranks() {
        // given
        Set<ZSetOperations.TypedTuple<String>> mockSet = new LinkedHashSet<>();
        mockSet.add(createTuple("concert1", 100.0));
        mockSet.add(createTuple("concert2", 90.0));
        mockSet.add(createTuple("concert3", 80.0));

        Mockito.when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        Mockito.when(zSetOperations.reverseRangeWithScores("concert:rank", 0, 4)).thenReturn(mockSet);

        // when
        Map<String, Long> result = popularService.getRanks();

        // then
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(100L, result.get("concert1"));
        Assertions.assertEquals(90L, result.get("concert2"));
        Assertions.assertEquals(80L, result.get("concert3"));
    }

    private ZSetOperations.TypedTuple<String> createTuple(String value, double score) {
        return new DefaultTypedTuple<>(value, score);
    }
}

