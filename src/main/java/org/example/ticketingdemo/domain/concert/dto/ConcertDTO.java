package org.example.ticketingdemo.domain.concert.dto;

import lombok.Data;

@Data
public class ConcertDTO {
    private Long concertId;
    private String title;
    private String category;
    private String description;
    private Double price;
}