package org.example.ticketingdemo.domain.seat.repository;

import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    @Query("SELECT s FROM Seat s " +
            "JOIN FETCH s.concert c " +
            "LEFT JOIN FETCH s.user u " +
            "WHERE c.concertId = :concertId AND s.seatNumber = :seatNumber")
    Optional<Seat> findByConcertIdAndSeatNumber(Long concertId, String seatNumber);



    @Query("SELECT s FROM Seat s " +
            "JOIN FETCH s.concert c " +
            "LEFT JOIN FETCH s.user u " +
            "WHERE c.concertId = :concertId AND s.status = :status")
    List<Seat> findAllByConcertIdAndStatus(Long concertId, SeatStatus status);
}
