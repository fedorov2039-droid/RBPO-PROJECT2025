package com.example.cinema.controller;

import com.example.cinema.model.Screening;
import com.example.cinema.model.Ticket;
import com.example.cinema.service.CinemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinema")
public class CinemaOperationsController {

    private final CinemaService service;

    public CinemaOperationsController(CinemaService service) {
        this.service = service;
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyTicket(@RequestParam Long userId, @RequestParam Long screeningId) {
        try {
            Ticket ticket = service.buyTicket(userId, screeningId);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return/{ticketId}")
    public ResponseEntity<?> returnTicket(@PathVariable Long ticketId) {
        try {
            service.returnTicket(ticketId);
            return ResponseEntity.ok("Билет возвращен успешно");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cancel-screening/{id}")
    public ResponseEntity<?> cancelScreening(@PathVariable Long id) {
        service.cancelScreening(id);
        return ResponseEntity.ok("Сеанс отменен, билеты возвращены");
    }

    @GetMapping("/search")
    public List<Screening> search(@RequestParam double maxPrice) {
        return service.findAffordableScreenings(maxPrice);
    }

    @GetMapping("/stats/{screeningId}")
    public ResponseEntity<String> getStats(@PathVariable Long screeningId) {
        return ResponseEntity.ok(service.getOccupancyStats(screeningId));
    }
}