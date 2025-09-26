package org.example.ticketingdemo.domain.seat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.ticketingdemo.common.entity.BaseEntity;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.exception.InvaildSeatException;
import org.example.ticketingdemo.domain.seat.exception.SeatErrorCode;
import org.example.ticketingdemo.domain.user.entity.User;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "seat")
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Column(nullable = false)
    private String seatNumber;

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

    public void cancelBy(User user) {
        if (this.status != SeatStatus.SOLD) { // 해당 시트가 판매된 시트가 아닐 떄
            throw new InvaildSeatException(SeatErrorCode.SEAT_NOT_SOLD);
        }
        if (!this.user.getId().equals(user.getId())) { // 유저가 일치하지 않을 떄
            throw new InvaildSeatException(SeatErrorCode.SEAT_NOT_MATCH_USER);
        }
        this.user = null;
        this.status = SeatStatus.AVAILABLE;
    }

    public static Seat create(Concert concert, String seatNumber) {
        return new Seat(concert, seatNumber);
    }
}
