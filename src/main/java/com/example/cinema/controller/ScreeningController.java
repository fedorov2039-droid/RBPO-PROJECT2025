package com.example.cinema.controller;

import com.example.cinema.model.Screening;
import com.example.cinema.repository.ScreeningRepository;
import com.example.cinema.service.CinemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {

    private final ScreeningRepository screeningRepository;
    private final CinemaService cinemaService;

    public ScreeningController(ScreeningRepository screeningRepository, CinemaService cinemaService) {
        this.screeningRepository = screeningRepository;
        this.cinemaService = cinemaService;
    }

    @GetMapping
    public List<Screening> getAllScreenings() {
        return screeningRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createScreening(@RequestBody Screening screening) {
        try {
            return ResponseEntity.ok(cinemaService.createScreening(screening));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelScreening(@PathVariable Long id) {
        try {
            cinemaService.cancelScreening(id);
            return ResponseEntity.ok("Сеанс отменен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}