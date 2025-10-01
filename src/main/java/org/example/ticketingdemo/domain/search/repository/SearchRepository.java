package org.example.ticketingdemo.domain.search.repository;

import jakarta.persistence.LockModeType;
import org.example.ticketingdemo.domain.search.entity.Popular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SearchRepository extends JpaRepository<Popular, Long> {
    @Query("SELECT p FROM Popular p WHERE p.id = :id")
    Optional<Popular> findByIdForUpdate(@Param("id") long id);

}