package org.example.ticketingdemo.domain.payment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.ticketingdemo.common.entity.BaseEntity;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.user.entity.User;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

//    @Column(nullable = false)   // 나중에 간편결제 기능 추가할거 대비해서 일단 주석처리
//    private String paymentMethod;

    @Column(nullable = false)
    private Double totalPrice;

    @Builder
    public Payment(User user, Seat seat, Double totalPrice) {
     this.user = user;
     this.seat = seat;
     this.totalPrice = totalPrice;
    }
}
