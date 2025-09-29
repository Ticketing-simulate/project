package org.example.ticketingdemo.domain.seat.supabase;

import org.example.ticketingdemo.domain.seat.entity.Seat;

import java.util.Optional;

public interface SeatExternalService {
    // Supabase에서 좌석 정보(티켓 정보)를 ID로 조회
    Optional<Seat> findSeatById(Long id);

    // Spring에서 생성된 좌석 정보를 Supabase에 등록
    void createSeat(Seat seat);
}