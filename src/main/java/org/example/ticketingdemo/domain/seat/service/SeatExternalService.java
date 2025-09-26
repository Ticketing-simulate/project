package org.example.ticketingdemo.domain.seat.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.exception.InvaildSeatException;
import org.example.ticketingdemo.domain.seat.exception.SeatErrorCode;
import org.example.ticketingdemo.domain.seat.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatExternalService  {
    private final SeatRepository seatRepository;

    @Transactional // concert에서 seat 생성할 때
    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Transactional
    public Seat getSeatById(Long seatId) {
        return seatRepository.findById(seatId).orElseThrow(()-> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_SEAT));
    }
}
