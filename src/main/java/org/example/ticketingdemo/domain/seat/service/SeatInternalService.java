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
import org.example.ticketingdemo.lock.RedisLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.ticketingdemo.lock.LockService;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatInternalService {

    private final SeatRepository seatRepository;
    private final UserRepository userRepository;


    // Redis 락 적용 구매
    @Transactional
    @RedisLock(key = "seat_lock:{#concertId}:{#seatBuyRequest.seatNumber}")
    public SeatBuyResponse buySeatWithRedisLock(SeatBuyRequest seatBuyRequest, Long userId, Long concertId) {
        // 유저 조회
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_USER_ID));

        // 좌석 조회
        Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatBuyRequest.seatNumber())
                .orElseThrow(() -> new InvaildSeatException(SeatErrorCode.SEAT_NOT_FOUND_SEAT));

        // 이미 SOLD or PENDING 좌석이면 예외 발생
        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_ALREADY_SOLD);
        }
        if (seat.getStatus() == SeatStatus.PENDING) {
            throw new InvaildSeatException(SeatErrorCode.SEAT_ALREADY_PENDING);
        }

        // 좌석을 구매 대기 상태로 변경
        seat.assignUser(user);
        seat.changeStatus(SeatStatus.PENDING);

        //DB에 즉시 반영 (트랜잭션 커밋 전에 flush)
        seatRepository.saveAndFlush(seat);

        return SeatBuyResponse.from(seat, SeatUserResponse.from(seat), SeatConcertResponse.from(seat));
    }

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
        seat.assignUser(user);
        seat.changeStatus(SeatStatus.PENDING);

        // DB에 즉시 반영
        seatRepository.saveAndFlush(seat);

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

    // 팔리지 않은(AVAILABLE)한 좌석만 출력
    @Transactional(readOnly = true)
    public List<String> getAvailableSeatNumbers(Long concertId) {
        return seatRepository.findAllByConcertIdAndStatus(concertId, SeatStatus.AVAILABLE)
                .stream()
                .map(Seat::getSeatNumber) // seatNumber는 int
                .toList();
    }
}
