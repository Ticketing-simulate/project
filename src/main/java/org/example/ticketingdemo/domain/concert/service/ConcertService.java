package org.example.ticketingdemo.domain.concert.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.concert.dto.ConcertDTO;
import org.example.ticketingdemo.domain.concert.entity.Concert;

import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.example.ticketingdemo.domain.search.dto.ConcertsSearchDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

//import static java.util.stream.Nodes.collect;

@Service

public interface ConcertService {
//    private final ConcertRepository concertRepository;

    ConcertDTO createConcert(ConcertDTO concertDTO);
    ConcertDTO getConcertById(Long id);
    List<ConcertDTO> getAllConcerts();
    ConcertDTO updateConcert(Long id, ConcertDTO concertDTO);
    void deleteConcert(Long id);

//    public default List<ConcertsSearchDto> ConcertSearch(String query, int limit) {
//        List<Concert> concerts = concertRepository.findByTitleContaingIgnoreCase(query, PageRequest.of(0, limit));
//
//        return concerts.stream().map(ConcertsSearchDto::from).collect(Collectors.toList());
//    }
}