package com.example.cinema.service;

import com.example.cinema.model.*;
import com.example.cinema.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CinemaService {

    private final TicketRepository ticketRepository;
    private final ScreeningRepository screeningRepository;
    private final CustomerRepository customerRepository;
    private final HallRepository hallRepository;
    private final MovieRepository movieRepository;

    public CinemaService(
            TicketRepository ticketRepository,
            ScreeningRepository screeningRepository,
            CustomerRepository customerRepository,
            HallRepository hallRepository,
            MovieRepository movieRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.screeningRepository = screeningRepository;
        this.customerRepository = customerRepository;
        this.hallRepository = hallRepository;
        this.movieRepository = movieRepository;
    }

    // 1) Покупка билета
    @Transactional
    public Ticket buyTicket(Long customerId, Long screeningId, Integer seatNumber) {
        if (seatNumber == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "seatNumber обязателен");
        }

        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сеанс не найден"));

        if (screening.isCancelled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Сеанс отменён");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Клиент не найден"));

        Hall hall = screening.getHall();
        int capacity = hall.getCapacity();

        if (seatNumber < 1 || seatNumber > capacity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Место должно быть в диапазоне 1.." + capacity);
        }

        long soldTickets = ticketRepository.countByScreening_IdAndRefundedFalse(screeningId);
        if (soldTickets >= capacity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "В зале нет свободных мест");
        }

        boolean seatTaken = ticketRepository.existsByScreening_IdAndSeatNumberAndRefundedFalse(screeningId, seatNumber);
        if (seatTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Место уже занято");
        }

        Ticket ticket = new Ticket();
        ticket.setScreening(screening);
        ticket.setCustomer(customer);
        ticket.setSeatNumber(seatNumber);
        ticket.setRefunded(false);

        return ticketRepository.save(ticket);
    }

    // 2) Возврат билета до начала сеанса
    @Transactional
    public Ticket returnTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Билет не найден"));

        if (ticket.isRefunded()) {
            return ticket;
        }

        LocalDateTime start = ticket.getScreening().getStartTime();
        if (!LocalDateTime.now().isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Возврат возможен только до начала сеанса");
        }

        ticket.setRefunded(true);
        return ticketRepository.save(ticket);
    }

    // 3) Отмена сеанса: cancelled=true + refunded=true всем билетам
    @Transactional
    public Screening cancelScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сеанс не найден"));

        screening.setCancelled(true);
        screeningRepository.save(screening);

        List<Ticket> tickets = ticketRepository.findAllByScreening_Id(screeningId);
        for (Ticket t : tickets) {
            t.setRefunded(true);
        }
        ticketRepository.saveAll(tickets);

        return screening;
    }

    // 4) Перенос сеанса
    @Transactional
    public Screening rescheduleScreening(Long screeningId, LocalDateTime newStartTime) {
        if (newStartTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "newStartTime обязателен");
        }

        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сеанс не найден"));

        if (screening.isCancelled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Нельзя перенести отменённый сеанс");
        }

        Long hallId = screening.getHall().getId();

        boolean conflict = screeningRepository.existsByHall_IdAndStartTime(hallId, newStartTime);
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "В этом зале уже есть сеанс на это время");
        }

        screening.setStartTime(newStartTime);
        return screeningRepository.save(screening);
    }

    // 5) Создать сеанс и заблокировать места (через SYSTEM customer)
    @Transactional
    public Screening createScreeningWithBlockedSeats(
            Long movieId,
            Long hallId,
            LocalDateTime startTime,
            double price,
            Long systemCustomerId,
            List<Integer> seatsToBlock
    ) {
        if (movieId == null || hallId == null || startTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "movieId, hallId, startTime обязательны");
        }
        if (price < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price должен быть >= 0");
        }
        if (systemCustomerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "systemCustomerId обязателен");
        }

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден"));

        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Зал не найден"));

        Customer systemCustomer = customerRepository.findById(systemCustomerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SYSTEM клиент не найден"));

        boolean conflict = screeningRepository.existsByHall_IdAndStartTime(hallId, startTime);
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "В этом зале уже есть сеанс на это время");
        }

        Screening screening = new Screening();
        screening.setMovie(movie);
        screening.setHall(hall);
        screening.setStartTime(startTime);
        screening.setPrice(price);
        screening.setCancelled(false);

        Screening saved = screeningRepository.save(screening);

        if (seatsToBlock != null) {
            for (Integer seat : seatsToBlock) {
                if (seat == null) continue;

                if (seat < 1 || seat > hall.getCapacity()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Место вне диапазона: " + seat);
                }

                boolean taken = ticketRepository.existsByScreening_IdAndSeatNumberAndRefundedFalse(saved.getId(), seat);
                if (taken) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Место уже занято: " + seat);
                }

                Ticket t = new Ticket();
                t.setScreening(saved);
                t.setCustomer(systemCustomer);
                t.setSeatNumber(seat);
                t.setRefunded(false);
                ticketRepository.save(t);
            }
        }

        return saved;
    }
}
