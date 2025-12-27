package com.example.cinema.controller;

import com.example.cinema.model.Hall;
import com.example.cinema.repository.HallRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {
    private final HallRepository repository;

    public HallController(HallRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Hall> getAll() { return repository.findAll(); }

    @PostMapping
    public Hall create(@RequestBody Hall hall) { return repository.save(hall); }

    @PutMapping("/{id}")
    public ResponseEntity<Hall> update(@PathVariable Long id, @RequestBody Hall details) {
        return repository.findById(id).map(hall -> {
            hall.setName(details.getName());
            hall.setCapacity(details.getCapacity());
            return ResponseEntity.ok(repository.save(hall));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}