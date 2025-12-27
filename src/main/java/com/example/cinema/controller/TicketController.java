package com.example.cinema.controller;

import com.example.cinema.model.Ticket;
import com.example.cinema.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository repository;

    public TicketController(TicketRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Ticket> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Ticket create(@RequestBody Ticket ticket) {
        return repository.save(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket details) {
        return repository.findById(id).map(ticket -> {
            ticket.setScreening(details.getScreening());
            ticket.setCustomer(details.getCustomer());
            ticket.setRefunded(details.isRefunded()); // Используем стандартный геттер для boolean
            return ResponseEntity.ok(repository.save(ticket));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}