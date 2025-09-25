package org.example.ticketingdemo.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.example.ticketingdemo.domain.search.repository.SearchRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularService {

    private final SearchRepository searchRepository;

    public Popular savaPopular(Popular popular) {
        Popular savedPopular = new Popular(
                popular.getId(),
                popular.getConcertId(),
                popular.getTicketcounts()
        );

       return searchRepository.save(savedPopular);
    }
}
