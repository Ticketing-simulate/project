package org.example.ticketingdemo.domain.seat.supabase.dto;

import org.example.ticketingdemo.domain.seat.entity.Seat;

/**
 * Supabase(또는 REST)로 보낼 요청용 DTO.
 * - record를 사용하여 불변(immutable) DTO로 선언.
 * - JSON 키는 Supabase 테이블 컬럼명(예: concert_id, user_id)을 따르게 @JsonProperty로 매핑.
 * - null 값은 전송하지 않으려면 @JsonInclude(Include.NON_NULL) 사용.
 */

public record SeatRequest(
        Long id,
        Long userId,
        Long concertId,
        String status,
        String seatNumber
) {

    /**
     * Entity -> DTO 변환 유틸리티
     * - 연관관계(User, Concert)는 엔티티 전체를 보내지 않고 ID(Long)만 추출해서 보냄.
     * - Enum은 name()으로 문자열 변환.
     *
     * 주의:
     * - seat.getUser() 또는 seat.getConcert()가 null일 수 있으니 null 체크 필수.
     */

    public static SeatRequest fromEntity(Seat seat) {
        return new SeatRequest(
                seat.getId(),
                seat.getUser() != null ? seat.getUser().getId() : null,
                seat.getConcert() != null ? seat.getConcert().getConcertId() : null,
                seat.getStatus() != null ? seat.getStatus().name() : null,
                seat.getSeatNumber()
        );
    }
}