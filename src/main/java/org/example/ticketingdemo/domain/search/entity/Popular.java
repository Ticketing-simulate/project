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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long concertId;

    private Integer ticketcounts;

    @Builder
    public Popular(Long id, Long concertId, Integer ticketcounts) {
        this.id = id;
        this.concertId = concertId;
        this.ticketcounts = ticketcounts;
    }

}
