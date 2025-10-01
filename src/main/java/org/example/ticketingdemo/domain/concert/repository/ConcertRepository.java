package org.example.ticketingdemo.domain.concert.repository;

import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;


@Repository

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    Page<Concert> findByTitle(String title, Pageable pageable);
}

