package org.example.ticketingdemo.domain.search.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "popular")
public class Popular {

    /*
    Id - PK로 설정하기
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    //@Id * 테스트를 위해 주석처리..
    private Long concertId;

    /*
    티켓수 - 티켓 사는 개수 (콘서트)
     */
    private Integer ticketcounts;

    @Builder
    public Popular(Long id, Integer ticketcounts) {
        this.id = id;
        this.ticketcounts = ticketcounts;
    }

}
