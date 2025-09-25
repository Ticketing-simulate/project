package org.example.ticketingdemo.domain.concert.repository;

import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//JpaRepository를 상속받아 기본적인 데이터베이스 작업(CRUD) 메서드들을 자동으로 사용할 수 있음.
// <Concert, Long> : 이 리포지토리가 'Concert' 엔티티를 다루고, 엔티티의 ID 타입이 'Long'임을 의미.
public interface ConcertRepository extends JpaRepository<Concert, Long> {
}

//리포지토리의 역할?
//리포지토리는 데이터베이스와 통신.
//컨트롤러가 사용자 요청을 받아서 서비스에게 넘겨주면,
//서비스가 실제로 데이터를 저장하거나 조회, 수정, 삭제하는 일을 리포지토리에게 맡김.