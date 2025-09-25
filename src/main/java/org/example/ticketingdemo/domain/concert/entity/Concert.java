package org.example.ticketingdemo.domain.concert.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 100)
    private String title;   // 콘서트 제목

    @Column(nullable = false, length = 50)
    private String category; // 콘서트 장르

    @Column(columnDefinition = "TEXT")
    private String description; // 콘서트 설명

    @Column(nullable = false)
    private Double price;   // 티켓 가격

    @Column(nullable = false)
    private Integer seat;   // 좌석 수

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록일

    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일

    // 예: 판매 가능 티켓 수 (stock)
    @Column(nullable = false)
    private Integer ticket;
}