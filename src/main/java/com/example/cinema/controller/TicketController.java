package com.example.cinema.controller;

import com.example.cinema.model.Ticket;
import com.example.cinema.repository.TicketRepository;
import com.example.cinema.service.CinemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final CinemaService cinemaService;

    public TicketController(TicketRepository ticketRepository, CinemaService cinemaService) {
        this.ticketRepository = ticketRepository;
        this.cinemaService = cinemaService;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> buyTicket(@RequestBody Map<String, Long> request) {
        try {
            Ticket ticket = cinemaService.buyTicket(request.get("screeningId"), request.get("customerId"));
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/group")
    public ResponseEntity<?> buyGroupTickets(@RequestBody Map<String, Object> request) {
        try {
            Long screeningId = Long.valueOf(request.get("screeningId").toString());
            Long customerId = Long.valueOf(request.get("customerId").toString());
            int count = Integer.parseInt(request.get("count").toString());

            cinemaService.buyGroupTickets(screeningId, customerId, count);
            return ResponseEntity.ok("Групповая покупка успешна!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> returnTicket(@PathVariable Long id) {
        try {
            cinemaService.returnTicket(id);
            return ResponseEntity.ok("Билет возвращен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}