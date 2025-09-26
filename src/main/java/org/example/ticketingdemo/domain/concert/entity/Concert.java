package org.example.ticketingdemo.domain.concert.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.ticketingdemo.domain.seat.entity.Seat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "concerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertId; // 콘서트 고유 ID

    // nullable = false 을 사용하여 데이터베이스의 해당 칼럼에 빈 값(NULL) 허용하지 않음.
    @Column(nullable = false, length = 100)
    private String title;   // 콘서트 제목

    @Column(nullable = false, length = 50)
    private String category; // 콘서트 장르

    @Column(columnDefinition = "TEXT")
    private String description; // 콘서트 설명

    @Column(nullable = false)
    private Double price;   // 티켓 가격

    @Column(nullable = false)
    private Integer seat;   // 총 좌석 수


    // 1:N 관계 (하나의 콘서트는 여러 개의 좌석을 가질 수 있음)
    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록일

    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일

    // 판매 가능 티켓 수
    @Column(nullable = false)
    private Integer ticket;

}