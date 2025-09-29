package org.example.ticketingdemo.domain.concert.controller;

import org.example.ticketingdemo.domain.concert.dto.ConcertDTO;
import org.example.ticketingdemo.domain.concert.service.ConcertService;
import org.example.ticketingdemo.domain.search.dto.ConcertsSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concerts")
public class ConcertController {
    private final ConcertService concertService;

    @Autowired
    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    // 콘서트 생성
    @PostMapping
    public ResponseEntity<ConcertDTO> createConcert(@RequestBody ConcertDTO concertDTO) {
        ConcertDTO newConcert = concertService.createConcert(concertDTO);
        return new ResponseEntity<>(newConcert, HttpStatus.CREATED);
    }

    // id로 개별 콘서트 조회
    @GetMapping("/{id}")
    public ResponseEntity<ConcertDTO> getConcertById(@PathVariable Long id) {
        ConcertDTO concert = concertService.getConcertById(id);
        return ResponseEntity.ok(concert);
    }

    // 모든 콘서트 조회
    @GetMapping
    public ResponseEntity<List<ConcertDTO>> getAllConcerts() {
        List<ConcertDTO> concerts = concertService.getAllConcerts();
        return ResponseEntity.ok(concerts);
    }

    // id로 개별 콘서트 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<ConcertDTO> updateConcert(@PathVariable Long id, @RequestBody ConcertDTO concertDTO) {
        ConcertDTO updatedConcert = concertService.updateConcert(id, concertDTO);
        return ResponseEntity.ok(updatedConcert);
    }

    // 콘서트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable Long id) {
        concertService.deleteConcert(id);
        return ResponseEntity.noContent().build();
    }
}
