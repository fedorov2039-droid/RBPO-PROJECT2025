package com.example.cinema.controller;

import com.example.cinema.model.Hall;
import com.example.cinema.repository.HallRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    private final HallRepository hallRepository;

    public HallController(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @GetMapping
    public List<Hall> getAllHalls() {
        return hallRepository.findAll();
    }

    @PostMapping
    public Hall createHall(@RequestBody Hall hall) {
        return hallRepository.save(hall);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        if (hallRepository.existsById(id)) {
            hallRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}