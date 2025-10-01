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

public interface ConcertService {


    ConcertDTO createConcert(ConcertDTO concertDTO);

    ConcertDTO getConcertById(Long id);

    List<ConcertDTO> getAllConcerts();

    ConcertDTO updateConcert(Long id, ConcertDTO concertDTO);

    void deleteConcert(Long id);

}