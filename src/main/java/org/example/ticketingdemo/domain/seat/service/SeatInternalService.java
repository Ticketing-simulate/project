package org.example.ticketingdemo.domain.seat.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.seat.dto.request.SeatBuyRequest;
import org.example.ticketingdemo.domain.seat.dto.request.SeatCancelRequest;
import org.example.ticketingdemo.domain.seat.dto.response.SeatBuyResponse;
import org.example.ticketingdemo.domain.seat.dto.response.SeatCancelResponse;
import org.example.ticketingdemo.domain.seat.dto.response.SeatConcertResponse;
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
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatInternalService {
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional // 좌석 구매
    public SeatBuyResponse buySeat(SeatBuyRequest seatBuyRequest, Long userId, Long concertId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatBuyRequest.seatNumber())
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_SEAT));

        // 이미 팛린 좌석인지 확인
        if(seat.getStatus() == SeatStatus.SOLD) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_ALREADY_SOLD);
        }
        seat.assignUser(user); // 유저와 연결
        seat.changeStatus(SeatStatus.SOLD); // 구매 되어 seat 상태 sold 변경
        return SeatBuyResponse.from(seat, SeatUserResponse.from(seat), SeatConcertResponse.from(seat));
    }

    @Transactional // 좌석 구매 취소
    public SeatCancelResponse cancelSeat(SeatCancelRequest seatCancelRequest, Long userId, Long concertId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        // 콘서트 + 좌석 번호로 좌석 조회
        Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatCancelRequest.seatNumber())
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_SEAT));

        seat.cancelBy(user);

        return SeatCancelResponse.from(seat, SeatConcertResponse.from(seat));
    }

    // 팔리지 않은(ACAILABLE)한 좌석만 출력
    @Transactional(readOnly = true)
    public List<String> getAvailableSeatNumbers(Long concertId) {
        return seatRepository.findAllByConcertIdAndStatus(concertId, SeatStatus.AVAILABLE)
                .stream()
                .map(Seat::getSeatNumber) // seatNumber는 int
                .toList();
    }
}
