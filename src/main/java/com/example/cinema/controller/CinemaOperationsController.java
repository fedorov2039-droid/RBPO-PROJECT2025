package com.example.cinema.controller;

import com.example.cinema.model.Screening;
import com.example.cinema.model.Ticket;
import com.example.cinema.service.CinemaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cinema")
public class CinemaOperationsController {

    private final CinemaService cinemaService;

    public CinemaOperationsController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping("/tickets/buy")
    public Ticket buyTicket(
            @RequestParam Long customerId,
            @RequestParam Long screeningId,
            @RequestParam Integer seatNumber
    ) {
        return cinemaService.buyTicket(customerId, screeningId, seatNumber);
    }

    @PostMapping("/tickets/{ticketId}/return")
    public Ticket returnTicket(@PathVariable Long ticketId) {
        return cinemaService.returnTicket(ticketId);
    }

    @PostMapping("/screenings/{id}/cancel")
    public Screening cancelScreening(@PathVariable Long id) {
        return cinemaService.cancelScreening(id);
    }

    @PostMapping("/screenings/{id}/reschedule")
    public Screening rescheduleScreening(
            @PathVariable Long id,
            @RequestParam LocalDateTime newStartTime
    ) {
        return cinemaService.rescheduleScreening(id, newStartTime);
    }

    @PostMapping("/screenings/create-with-blocked-seats")
    public Screening createScreeningWithBlockedSeats(
            @RequestParam Long movieId,
            @RequestParam Long hallId,
            @RequestParam LocalDateTime startTime,
            @RequestParam double price,
            @RequestParam Long systemCustomerId,
            @RequestParam(required = false) List<Integer> seatsToBlock
    ) {
        return cinemaService.createScreeningWithBlockedSeats(
                movieId,
                hallId,
                startTime,
                price,
                systemCustomerId,
                seatsToBlock
        );
    }
}
