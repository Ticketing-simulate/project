package org.example.ticketingdemo.domain.search.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class ConcertsSearchDto {

    private Long concertId;
    private String title;
    private String category;
    private String description;
    private Double price;
    private Integer seat;
    private Integer ticket;

    @Builder
    public ConcertsSearchDto(Long concertId, String title, String category, String description, Double price, Integer seat, Integer ticket) {
        this.concertId = concertId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.seat = seat;
        this.ticket = ticket;
    }

    public ConcertsSearchDto(List<ConcertsSearchDto> concerts2, long totalElements, int totalPages, int size, int number) {
    }
}
