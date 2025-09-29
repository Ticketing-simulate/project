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

    @Transactional // 좌석 구매 요청 (결제 대기)
    public SeatBuyResponse buySeat(SeatBuyRequest seatBuyRequest, Long userId, Long concertId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        // 좌석 조회
        Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatBuyRequest.seatNumber())
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_SEAT));

        // 이미 SOLD or PENDING 좌석이면 구매 불가
        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_ALREADY_SOLD);
        }
        if (seat.getStatus() == SeatStatus.PENDING) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_ALREADY_PENDING);
        }
        // 좌석을 구매 대기 상태로 전환
        seat.markPending(user);
        return SeatBuyResponse.from(seat, SeatUserResponse.from(seat), SeatConcertResponse.from(seat));
    }

    @Transactional // 좌석 구매 취소(시간 초과 및 PENDING 상태인 SEAT를 취소한 경우
    public SeatCancelResponse cancelSeat(SeatCancelRequest seatCancelRequest, Long userId, Long concertId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        // 콘서트 + 좌석 번호로 좌석 조회
        Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatCancelRequest.seatNumber())
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_SEAT));

        seat.cancelPending(user);

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
