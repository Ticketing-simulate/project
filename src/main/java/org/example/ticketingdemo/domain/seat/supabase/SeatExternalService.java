package org.example.ticketingdemo.domain.seat.supabase;

import org.example.ticketingdemo.domain.seat.entity.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatExternalService {

    // Supabase에서 좌석 정보(티켓 정보)를 ID로 조회
    Optional<Seat> findSeatById(Long seatId);

    // Supabase에서 콘서트 ID로 좌석 목록 조회
    List<Seat> findSeatsByConcertId(Long concertId);

    // Spring에서 생성된 좌석 정보를 Supabase에 등록
    void createSeat(Seat seat);

    // 좌석 상태 업데이트
    void updateSeat(Seat seat);
}