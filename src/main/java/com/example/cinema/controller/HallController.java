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

    public HallController(HallRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Hall> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hall> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Hall create(@RequestBody Hall hall) {
        return repository.save(hall);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hall> update(@PathVariable Long id, @RequestBody Hall body) {
        return repository.findById(id)
                .map(h -> {
                    h.setName(body.getName());
                    h.setCapacity(body.getCapacity());
                    return ResponseEntity.ok(repository.save(h));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
