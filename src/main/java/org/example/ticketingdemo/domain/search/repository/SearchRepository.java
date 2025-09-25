package org.example.ticketingdemo.domain.search.repository;

import org.example.ticketingdemo.domain.search.entity.Popular;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Popular, Long> {

}
