package org.example.ticketingdemo.domain.search.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "popular")
public class Popular {

    /*
    Id - PK로 설정하기
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Id
    private Long concertId;

    /*
    티켓수 - 티켓 사는 개수 (콘서트)
     */
    private Integer ticketcounts;

    @Builder
    public Popular(Long id, Long concertId, Integer ticketcounts) {
        this.id = id;
        this.concertId = concertId;
        this.ticketcounts = ticketcounts;
    }

}
