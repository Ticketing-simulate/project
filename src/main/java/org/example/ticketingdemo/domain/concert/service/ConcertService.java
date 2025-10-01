package org.example.ticketingdemo.domain.concert.service;


import org.example.ticketingdemo.domain.concert.dto.ConcertDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service

public interface ConcertService {

    ConcertDTO createConcert(ConcertDTO concertDTO);

    ConcertDTO getConcertById(Long id);

    List<ConcertDTO> getAllConcerts();

    ConcertDTO updateConcert(Long id, ConcertDTO concertDTO);

    void deleteConcert(Long id);

}