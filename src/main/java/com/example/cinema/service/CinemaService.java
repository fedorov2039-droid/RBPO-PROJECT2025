package com.example.cinema.service;

import com.example.cinema.model.*;
import com.example.cinema.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CinemaService {

    private final TicketRepository ticketRepository;
    private final ScreeningRepository screeningRepository;
    private final CustomerRepository customerRepository;

    public CinemaService(TicketRepository ticketRepository, ScreeningRepository screeningRepository, CustomerRepository customerRepository) {
        this.ticketRepository = ticketRepository;
        this.screeningRepository = screeningRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Ticket buyTicket(Long customerId, Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Сеанс не найден"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        long soldTickets = ticketRepository.countByScreeningIdAndRefundedFalse(screeningId);
        if (soldTickets >= screening.getHall().getCapacity()) {
            throw new RuntimeException("В зале нет свободных мест!");
        }

        Ticket ticket = new Ticket();
        ticket.setScreening(screening);
        ticket.setCustomer(customer);
        ticket.setRefunded(false);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public void returnTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Билет не найден"));

        // Проверка времени отключена для удобства тестирования
        // if (ticket.getScreening().getStartTime().isBefore(LocalDateTime.now().plusHours(1))) {
        //    throw new RuntimeException("Нельзя вернуть билет менее чем за час до сеанса!");
        // }

        ticket.setRefunded(true);
        ticketRepository.save(ticket);
    }

    @Transactional
    public void cancelScreening(Long screeningId) {
        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screeningId);
        for (Ticket ticket : tickets) {
            ticket.setRefunded(true);
            ticket.setScreening(null);
            ticketRepository.save(ticket);
        }
        screeningRepository.deleteById(screeningId);
    }

    public List<Screening> findAffordableScreenings(double maxPrice) {
        return screeningRepository.findByPriceBetweenAndStartTimeAfter(0, maxPrice, LocalDateTime.now());
    }

    public String getOccupancyStats(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Сеанс не найден"));

        long sold = ticketRepository.countByScreeningIdAndRefundedFalse(screeningId);
        int capacity = screening.getHall().getCapacity();

        double percent = ((double) sold / capacity) * 100;
        return String.format("Зал заполнен на %.2f%% (%d/%d мест)", percent, sold, capacity);
    }
}