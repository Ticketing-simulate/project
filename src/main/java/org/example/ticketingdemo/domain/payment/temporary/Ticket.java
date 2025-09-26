package org.example.ticketingdemo.domain.payment.temporary;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 임시 Ticket 엔티티
@Entity
@Getter
@Table(name = "tickets")
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String concertName;
    private Long price;

    @Builder
    public Ticket(String concertName, Long price) {
        this.concertName = concertName;
        this.price = price;
    }
}

