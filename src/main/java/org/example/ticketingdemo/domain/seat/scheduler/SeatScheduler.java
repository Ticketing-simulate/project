package org.example.ticketingdemo.domain.seat.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.seat.entity.Seat;
import org.example.ticketingdemo.domain.seat.enums.SeatStatus;
import org.example.ticketingdemo.domain.seat.repository.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatScheduler {
    private final SeatRepository seatRepository;
    /*
    1분마다 스케듈이 돌며 pending 상태에서3분이 지난 시트가 있는지 확인한다

    현재 Seat에서 plusMinutes(3)을 통해 현재 시간보다 3분이 추가되어 저장된다. 그렇기에
    PendingExpiresAtBefore 쿼리를 통해 현재이 seat의 PendingExpires보다 앞서 있다면 그것은 만료된 좌석이기에 list에 추가되어진다.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void releaseExpiredSeats() {
        List<Seat> expiredSeats =
                seatRepository.findAllByStatusAndPendingExpiresAtBefore(SeatStatus.PENDING, LocalDateTime.now());

        for (Seat seat : expiredSeats) {
            seat.releaseIfExpired();
        }
    }
}
