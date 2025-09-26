package org.example.ticketingdemo.domain.payment.repository;

import aj.org.objectweb.asm.commons.Remapper;
import org.example.ticketingdemo.domain.payment.dto.response.PaymentFindResponse;
import org.example.ticketingdemo.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByUserId(Long userId, Pageable pageable);

    Optional<Payment> findByIdAndUserId(Long userId, Long paymentId);
}
