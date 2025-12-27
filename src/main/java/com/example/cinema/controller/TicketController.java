package com.example.cinema.controller;

import com.example.cinema.model.Ticket;
import com.example.cinema.repository.TicketRepository;
import com.example.cinema.service.CinemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository repository;
    private final CinemaService cinemaService;

    public TicketController(TicketRepository repository, CinemaService cinemaService) {
        this.repository = repository;
        this.cinemaService = cinemaService;
    }

    @GetMapping
    public List<Ticket> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ticket create(@RequestBody Ticket ticket) {
        return repository.save(ticket);
    }

    @PostMapping("/buy")
    public Ticket buy(
            @RequestParam Long customerId,
            @RequestParam Long screeningId,
            @RequestParam Integer seatNumber
    ) {
        return cinemaService.buyTicket(customerId, screeningId, seatNumber);
    }

    @PostMapping("/{id}/return")
    public Ticket returnTicket(@PathVariable Long id) {
        return cinemaService.returnTicket(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
