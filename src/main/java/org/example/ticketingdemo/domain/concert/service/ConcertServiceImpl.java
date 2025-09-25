package org.example.ticketingdemo.domain.concert.service;

import org.example.ticketingdemo.domain.concert.dto.ConcertDTO;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertServiceImpl implements ConcertService {
    private final ConcertRepository concertRepository;

    @Autowired
    public ConcertServiceImpl(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Override
    public ConcertDTO createConcert(ConcertDTO concertDTO) {
        Concert concert = toEntity(concertDTO);
        Concert savedConcert = concertRepository.save(concert);
        return toDTO(savedConcert);
    }

    @Override
    public ConcertDTO getConcertById(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert not found with id " + id));
        return toDTO(concert);
    }

    @Override
    public List<ConcertDTO> getAllConcerts() {
        return concertRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ConcertDTO updateConcert(Long id, ConcertDTO concertDTO) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert not found with id " + id));

        concert.setTitle(concertDTO.getTitle());
        concert.setCategory(concertDTO.getCategory());
        concert.setDescription(concertDTO.getDescription());
        concert.setPrice(concertDTO.getPrice());

        Concert updatedConcert = concertRepository.save(concert);
        return toDTO(updatedConcert);
    }

    @Override
    public void deleteConcert(Long id) {
        concertRepository.deleteById(id);
    }

    // Helper methods for DTO-Entity conversion
    private Concert toEntity(ConcertDTO dto) {
        Concert entity = new Concert();
        entity.setTitle(dto.getTitle());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    private ConcertDTO toDTO(Concert entity) {
        ConcertDTO dto = new ConcertDTO();
        dto.setConcertId(entity.getConcertId());
        dto.setTitle(entity.getTitle());
        dto.setCategory(entity.getCategory());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        return dto;
    }
}
