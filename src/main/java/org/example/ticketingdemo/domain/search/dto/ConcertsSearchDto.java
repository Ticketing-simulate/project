package org.example.ticketingdemo.domain.search.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class ConcertsSearchDto {

    private Long concertId;
    private String title;
    private String category;
    private String description;
    private Double price;

    @Builder
    public ConcertsSearchDto(Long concertId, String title, String category, String description, Double price) {
        this.concertId = concertId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
    }
}
