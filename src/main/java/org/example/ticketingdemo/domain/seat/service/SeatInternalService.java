package org.example.ticketingdemo.domain.seat.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.example.ticketingdemo.domain.seat.dto.request.SeatBuyRequest;
import org.example.ticketingdemo.domain.seat.dto.response.SeatBuyResponse;

import org.example.ticketingdemo.domain.seat.dto.response.SeatUserResponse;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.exception.InvaildSeatException;
import org.example.ticketingdemo.domain.seat.exception.SeatErrorCode;
import org.example.ticketingdemo.domain.seat.repository.SeatRepository;
import org.example.ticketingdemo.domain.user.entity.User;
import org.example.ticketingdemo.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatInternalService {
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;

    @Transactional // Buy
    public SeatBuyResponse buySeat(SeatBuyRequest seatBuyRequest, Long userId, Long concertId) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        // Concert에서 생성된 좌석 조회
        Seat seat = seatRepository.findBySeatNumber(seatBuyRequest.seatNumber()).orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_SEAT));

        // 이미 팛린 좌석인지 확인
        if(seat.getStatus() == SeatStatus.SOLD) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_ALREADY_SOLD);
        }
        seat.assignUser(user); // 유저와 연결
        seat.changeStatus(SeatStatus.SOLD); // 구매 되어 seat 상태 sold 변경
        return SeatBuyResponse.from(seat, SeatUserResponse.from(seat));
    }
    @Transactional
    public SeatBuyResponse cancelSeat(Long seatId, Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        // 좌석 조회
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        // 이미 판매되지 않았다면 취소 불가
        if (seat.getStatus() != SeatStatus.SOLD) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_NOT_SOLD);
        }

        // 좌석 구매자가 본인인지 확인
        if (!seat.getUser().getId().equals(user.getId())) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_NOT_MATCH_USER);
        }

        // 취소 처리
        seat.assignUser(null);
        seat.changeStatus(SeatStatus.AVAILABLE);

        return SeatBuyResponse.from(seat, null);
    }
}
