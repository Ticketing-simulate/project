package org.example.ticketingdemo.domain.seat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.ticketingdemo.common.entity.BaseEntity;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.exception.InvaildSeatException;
import org.example.ticketingdemo.domain.seat.exception.SeatErrorCode;
import org.example.ticketingdemo.domain.user.entity.User;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "seat")
@Builder
@AllArgsConstructor
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concertId", nullable = false)
    private Concert concert;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Column(nullable = false)
    private String seatNumber;

    private LocalDateTime pendingExpiresAt;

    private Seat(Concert concert, String seatNumber) {
        this.concert = concert;
        this.status = SeatStatus.AVAILABLE; // concert에서 만들어질때(구매되기 전)
        this.seatNumber = seatNumber;
    }

    public void changeStatus(SeatStatus status){
        this.status = status;
    }

    public void assignUser(User user) {
        this.user = user;
    }

    public void cancelPending(User user) {
        if (this.status != SeatStatus.PENDING) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_NOT_PENDING);
        }
        if (!this.user.getId().equals(user.getId())) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_NOT_MATCH_USER);
        }
        this.user = null;
        this.status = SeatStatus.AVAILABLE;
    }

    public static Seat create(Concert concert, SeatStatus available, String seatNumber) {
        return new Seat(concert, seatNumber);
    }

    public void markPending(User user) { // 현재 좌석을 pending 상태로 바꾼다.
            this.user = user;
        this.status = SeatStatus.PENDING;
        this.pendingExpiresAt = LocalDateTime.now().plusMinutes(3); // 3분 타이머 설정
    }

    public boolean isPendingExpired() { // 다른 사람이 현재 좌석을 pending 하려고 할때, 해당 좌석의 pending 상태가 3분이 지났다면 true를 되돌린다.
        return this.status == SeatStatus.PENDING
                && this.pendingExpiresAt != null
                && this.pendingExpiresAt.isBefore(LocalDateTime.now());
    }

    public void releaseIfExpired() { // true라면 pending 되어 있는 좌석을 다시 AVAILABLE로 바꾼다.
        if (isPendingExpired()) {
            this.user = null;
            this.status = SeatStatus.AVAILABLE;
            this.pendingExpiresAt = null;
        }
    }
}
