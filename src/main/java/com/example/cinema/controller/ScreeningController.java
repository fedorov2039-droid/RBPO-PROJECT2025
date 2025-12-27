package com.example.cinema.controller;

import com.example.cinema.model.Screening;
import com.example.cinema.repository.ScreeningRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {

    private final ScreeningRepository repository;

    public ScreeningController(ScreeningRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Screening> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Screening> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Screening create(@RequestBody Screening screening) {
        return repository.save(screening);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Screening> update(@PathVariable Long id, @RequestBody Screening body) {
        return repository.findById(id)
                .map(s -> {
                    s.setMovie(body.getMovie());
                    s.setHall(body.getHall());
                    s.setStartTime(body.getStartTime());
                    s.setPrice(body.getPrice());
                    s.setCancelled(body.isCancelled());
                    return ResponseEntity.ok(repository.save(s));
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
