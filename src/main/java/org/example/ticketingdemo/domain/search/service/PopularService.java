package org.example.ticketingdemo.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.example.ticketingdemo.domain.search.repository.SearchRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularService {

    private final SearchRepository searchRepository;

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
}
