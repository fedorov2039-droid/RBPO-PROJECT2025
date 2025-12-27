package com.example.cinema.repository;

import com.example.cinema.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    boolean existsByHall_IdAndStartTime(Long hallId, LocalDateTime startTime);

    List<Screening> findByPriceBetweenAndStartTimeAfter(double minPrice, double maxPrice, LocalDateTime time);
}
