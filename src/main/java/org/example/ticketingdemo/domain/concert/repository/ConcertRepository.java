package org.example.ticketingdemo.domain.concert.repository;

import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;



@Repository

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    @Query("select c FROM Concert c JOIN c.title WHERE c.title = :query")
    Page<Concert> findByTitle(String query, Pageable pageable);
}

