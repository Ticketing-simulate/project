package org.example.ticketingdemo.domain.concert.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.concert.repository.ConcertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertTransactionalService {

    private final ConcertRepository concertRepository;

    /**
     * 콘서트 티켓 수를 감소시키는 메서드
     * - 같은 트랜잭션에서 find → 조건검사 → save 수행
     * - 부분적 원자성을 보장하기 위해 @Transactional 사용
     */
    @Transactional
    public boolean decreaseTicket(Long concertId, int qty) {
        // 콘서트 조회 (없으면 예외 발생)
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found"));

        // 티켓이 부족하면 false 반환
        if (concert.getTicket() < qty) {
            return false;
        }

        // 티켓 차감 후 저장
        concert.setTicket(concert.getTicket() - qty);
        concertRepository.save(concert);

        return true;
    }
}
