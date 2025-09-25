package org.example.ticketingdemo.domain.seat.dto.response;

import org.example.ticketingdemo.domain.seat.entity.Seat;

// 유저의 정보는 이름과 이메일만
public record SeatUserResponse(
        String userName,
        String email){

    public static SeatUserResponse from(Seat seat){
        return new SeatUserResponse(
                seat.getUser().getUserName(),
                seat.getUser().getEmail()
        );
    }
}
