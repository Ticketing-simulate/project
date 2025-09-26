package org.example.ticketingdemo.domain.concert.repository;

import org.example.ticketingdemo.domain.concert.entity.Concert;
import org.example.ticketingdemo.domain.search.dto.ConcertsSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {

//    @Query("select c From Concert c WHERE LOWER(c.title) Like LOWER(CONCAT('%', :name))")
//    List<Concert> findByTitleContaingIgnoreCase(@Param("title") String title, PageRequest pageRequest);
    Page<ConcertsSearchDto> searchs(Pageable pageable, String query);
}