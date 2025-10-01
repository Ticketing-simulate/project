package org.example.ticketingdemo.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.common.util.ErrorCodeEnum;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.search.dto.ConcertsSearchDto;
import org.example.ticketingdemo.domain.search.dto.SearchResponseDto;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.example.ticketingdemo.domain.search.service.PopularService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PopularContoller {

    private final PopularService popularService;

    /*
    {
  "id": 1,
  "ticketcounts": 100
   }
     */
    @PostMapping
    public ResponseEntity<Popular> savepopular(
            @RequestBody Popular popular
    ) {
        Popular popularticket = popularService.savePopular(popular);
        return ResponseEntity.ok(popularticket);
    }

    /*
    랭크 검색 (Redis 활용하여 만들예정) TOP5
     */
    @GetMapping("/searchs/ranks")
    public ResponseEntity<Map<String, Long>> getRanks() {
        Map<String, Long> RankResponse = popularService.getRanks();
        return ResponseEntity.ok(RankResponse);
    }

    /*
    콘서트, 티켓 검색 (두개 주제 검색하기)
    검색어는 치지않는 한, 콘서트하고 티켓이 같이 나옵니다
     */
//    @GetMapping("/search/concert")
//    public ResponseEntity<SearchResponseDto> getConcerts(
//            @RequestParam String query
//    ) {
//        if(query== null || query.isBlank()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        SearchResponseDto result = popularService.getSearchs(query);
//        return ResponseEntity.ok(result);
//    }


    /*
    페이징 처리
    query를 통해서 검색을 합니다 콘서트관련된것
    콘서트 title로 해서 query로 검색하는 것입니다 Concert테이블 title
     */
    @GetMapping("/search/concert")
    public ResponseEntity<Page<Concert>> getConcerts(
            @RequestParam String query,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size
    ) {
        if (ObjectUtils.isEmpty(query)) {
            throw new IllegalArgumentException("query is empty");
        }

        return ResponseEntity.ok(popularService.searchConcert(query, page, size));
    }

}
