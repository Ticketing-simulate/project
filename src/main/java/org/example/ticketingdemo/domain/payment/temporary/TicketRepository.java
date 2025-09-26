package org.example.ticketingdemo.domain.payment.temporary;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 임시 TicketRepository
// 셀제 DB 쿼리 대신 가짜 데이터를 반환
@Repository
@RequiredArgsConstructor
public class TicketRepository {

    private final EntityManager em;

    public Optional<Ticket> findById(Long id){
        //티켓 ID가 1번이면 임시 티켓 데이터 반환
        if (id.equals(1L)) {
            Ticket ticket = Ticket.builder()
                    .concertName("가짜 콘서트")
                    .price(25000L)
                    .build();
            em.persist(ticket); // 영속화 시켜줘야 Payment에서 참조 가능
            return Optional.of(ticket);
        }
        return Optional.empty();
    }
}
