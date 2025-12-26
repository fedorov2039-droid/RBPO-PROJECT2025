package com.example.cinema.controller;

import com.example.cinema.model.Ticket;
import com.example.cinema.repository.TicketRepository;
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
        // Простой POST, без бизнес-логики (как просит лаба 2)
        // JSON: { "screening": {"id": 1}, "customer": {"id": 1} }
        return repository.save(ticket);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}