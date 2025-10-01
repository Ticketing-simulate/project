package org.example.ticketingdemo.domain.search.service;



import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.example.ticketingdemo.domain.search.repository.SearchRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@RequiredArgsConstructor
public class PopularService {

    private final SearchRepository searchRepository;
    //RedisTemplate -> ZSet, Set, HashSet... 사용할때 적절함
    //readisTemplate는 주로 opsFor... 사용함
    //redisTemplate만들기 위해 주입이 필요함
    private final RedisTemplate<String, String> redisTemplate;
    private final ConcertRepository concertRepository;

    //rank설정
    private static String Rank = "concert:rank";

    @Transactional
    public void incrementTicketCountWithPessimisticLock(Long id) {
        Popular popular = searchRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Popular not found"));
        popular.setTicketcounts(popular.getTicketcounts() + 1);
    }

    @Transactional
    public Popular savePopular(Popular popular) {
        Popular existticket = searchRepository.findByIdForUpdate(popular.getId())
                .orElseThrow(() -> new EntityNotFoundException("Popular not found"));

        existticket.setTicketcounts(popular.getTicketcounts());
        return existticket;
    }


    /*
    Top 5 랭크 인기 콘서트 출력하기 (redisTemplate 활용)
    랭크는 Set형태 -> opsFor중에서 -> ZSet사용
    increment() 증가값
    reverseRange() -> rank, 처음, 끝 -> 랭크를 출력합니다
    */
    public Map<String, Long> getRanks() {
        Set<ZSetOperations.TypedTuple<String>> resultRanks =
                redisTemplate.opsForZSet().reverseRangeWithScores(Rank, 0, 4);

        Map<String, Long> ranks = new LinkedHashMap<>();
        if (resultRanks != null) {
            for (ZSetOperations.TypedTuple<String> tuple : resultRanks) {
                String concertId = tuple.getValue();
                Double score = tuple.getScore();
                if (concertId != null && score != null) {
                    ranks.put(concertId, score.longValue());
                }
            }
        }
        return ranks;
    }

        //query를 콘서트 검색하기(title)
       @Cacheable(value = "searchConcert", key="#query", condition = "#page>0")
        public Page<Concert> searchConcert(String query, int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            return concertRepository.findByTitle(query, pageable);
        }
}
