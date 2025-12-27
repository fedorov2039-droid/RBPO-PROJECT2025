package com.example.cinema.controller;

import com.example.cinema.model.Movie;
import com.example.cinema.repository.MovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieRepository repository;

    public MovieController(MovieRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Movie> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Movie create(@RequestBody Movie movie) {
        return repository.save(movie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> update(@PathVariable Long id, @RequestBody Movie body) {
        return repository.findById(id)
                .map(m -> {
                    m.setTitle(body.getTitle());
                    m.setDescription(body.getDescription());
                    m.setDurationMinutes(body.getDurationMinutes());
                    return ResponseEntity.ok(repository.save(m));
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
