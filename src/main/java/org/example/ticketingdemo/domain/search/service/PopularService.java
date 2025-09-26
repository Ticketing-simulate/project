package org.example.ticketingdemo.domain.search.service;

import com.fasterxml.jackson.databind.type.TypeModifier;
import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.example.ticketingdemo.domain.concert.service.ConcertService;
import org.example.ticketingdemo.domain.search.dto.ConcertsSearchDto;
import org.example.ticketingdemo.domain.search.dto.SearchResponseDto;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.example.ticketingdemo.domain.search.repository.SearchRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Long.parseLong;

@Service
@RequiredArgsConstructor
public class PopularService {

    private final SearchRepository searchRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ConcertService concertService;

    //rank설정
    private static String Rank = "concnert:rank";

    /*
    로그 기록 남기기 : Popular 저장하기
    이용객이 콘서트 티켓을 사서 얼마나 쌓았는지 저장합니다
     */
    public Popular savaPopular(Popular popular) {
        Popular savedPopular = new Popular(
                popular.getId(),
                popular.getConcertId(),
                popular.getTicketcounts()
        );

       return searchRepository.save(savedPopular);
    }

    //Top 5 랭크 인기 콘서트 출력하기
    public Map<String, Long> getRanks() {
        redisTemplate.opsForZSet().incrementScore(Rank, "concert:rank", 0);
        Set<String> resultRanks = redisTemplate.opsForZSet().reverseRange(Rank, 0, 4);

        Map<String, Long> ranks = new HashMap<>();
        if(resultRanks != null || resultRanks.isEmpty()) {
            for(String rank : resultRanks) {
                if(!rank.isEmpty()) {
                    ranks.put(rank ,parseLong(rank));
                }
            }
        }
        return ranks;
    }

    //콘서트 검색하기
//    public SearchResponseDto getSearchs(String query) {
//
//        List<ConcertsSearchDto> concerts = concertService.ConcertSearch(query, 100);
//
//        return SearchResponseDto.builder()
//                .concerts(concerts)
//                .build();
//    }

}
