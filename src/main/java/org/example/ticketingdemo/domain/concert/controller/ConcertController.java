package org.example.ticketingdemo.domain.concert.controller;

import org.example.ticketingdemo.domain.concert.dto.ConcertDTO;
import org.example.ticketingdemo.domain.concert.service.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {
    private final ConcertService concertService;

    @Autowired
    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @PostMapping
    public ResponseEntity<ConcertDTO> createConcert(@RequestBody ConcertDTO concertDTO) {
        ConcertDTO newConcert = concertService.createConcert(concertDTO);
        return new ResponseEntity<>(newConcert, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConcertDTO> getConcertById(@PathVariable Long id) {
        ConcertDTO concert = concertService.getConcertById(id);
        return ResponseEntity.ok(concert);
    }

    @GetMapping
    public ResponseEntity<List<ConcertDTO>> getAllConcerts() {
        List<ConcertDTO> concerts = concertService.getAllConcerts();
        return ResponseEntity.ok(concerts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConcertDTO> updateConcert(@PathVariable Long id, @RequestBody ConcertDTO concertDTO) {
        ConcertDTO updatedConcert = concertService.updateConcert(id, concertDTO);
        return ResponseEntity.ok(updatedConcert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable Long id) {
        concertService.deleteConcert(id);
        return ResponseEntity.noContent().build();
    }
}
