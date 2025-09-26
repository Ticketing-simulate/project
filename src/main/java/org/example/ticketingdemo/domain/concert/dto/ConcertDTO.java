package org.example.ticketingdemo.domain.concert.dto;

import lombok.Data;


@Data
// @Data 어노테이션은 Lombok 라이브러리에서 제공
//  getter, setter, toString(), equals(), hashCode() 등의 메서드를 자동으로 생성
public class ConcertDTO {
    private Long concertId;
    private String title;
    private String  category;
    private String description;
    private Double price;
    private Integer seatCount;
    private Integer ticketCount;
}