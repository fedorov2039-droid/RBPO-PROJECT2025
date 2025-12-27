package com.example.cinema.repository;

import com.example.cinema.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByScreeningIdAndRefundedFalse(Long screeningId);
    List<Ticket> findAllByScreeningId(Long screeningId);
}