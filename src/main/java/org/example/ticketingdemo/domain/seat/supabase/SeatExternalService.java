package org.example.ticketingdemo.domain.seat.supabase;

import org.example.ticketingdemo.domain.payment.temporary.Ticket;

import java.util.Optional;

public interface SeatExternalService {
    Optional<Ticket> findTicketById(Long id);
}