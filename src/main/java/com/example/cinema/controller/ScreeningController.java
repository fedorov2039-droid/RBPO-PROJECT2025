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

    @PostMapping
    public Screening create(@RequestBody Screening screening) {
        return repository.save(screening);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Screening> update(@PathVariable Long id, @RequestBody Screening details) {
        return repository.findById(id).map(screening -> {
            screening.setMovie(details.getMovie());
            screening.setHall(details.getHall());
            screening.setStartTime(details.getStartTime());
            screening.setPrice(details.getPrice());
            return ResponseEntity.ok(repository.save(screening));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}