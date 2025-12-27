package com.example.cinema.repository;

import com.example.cinema.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByScreening_IdAndSeatNumberAndRefundedFalse(Long screeningId, Integer seatNumber);

    long countByScreening_IdAndRefundedFalse(Long screeningId);

    List<Ticket> findAllByScreening_Id(Long screeningId);
}
