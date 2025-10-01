package org.example.ticketingdemo.domain.concert.repository;

import jakarta.persistence.LockModeType;
import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.search.dto.ConcertsSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//JpaRepository를 상속받아 기본적인 데이터베이스 작업(CRUD) 메서드들을 자동으로 사용할 수 있음.
// <Concert, Long> : 이 리포지토리가 'Concert' 엔티티를 다루고, 엔티티의 ID 타입이 'Long'임을 의미.
public interface ConcertRepository extends JpaRepository<Concert, Long> {
    @Query("select c FROM Concert c JOIN c.title WHERE c.title = :query")
    Page<Concert> findByTitle(String query, Pageable pageable);
}

