package org.example.ticketingdemo.domain.payment.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.ticketingdemo.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.id = :paymentId AND p.user.id = :userId AND p.deletedAt IS NULL")
    Optional<Payment> findByIdAndUserId(@Param("paymentId") Long paymentId, @Param("userId") Long userId);
}