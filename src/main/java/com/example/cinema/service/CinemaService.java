package com.example.cinema.service;

import com.example.cinema.model.Customer;
import com.example.cinema.model.Screening;
import com.example.cinema.model.Ticket;
import com.example.cinema.repository.CustomerRepository;
import com.example.cinema.repository.ScreeningRepository;
import com.example.cinema.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    public Ticket buyTicket(Long screeningId, Long customerId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Сеанс не найден!"));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Покупатель не найден!"));

        int soldTickets = ticketRepository.countByScreeningId(screeningId);
        if (soldTickets >= screening.getHall().getCapacity()) {
            throw new RuntimeException("Извините, все билеты проданы!");
        }

        Ticket ticket = new Ticket();
        ticket.setScreening(screening);
        ticket.setCustomer(customer);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void buyGroupTickets(Long screeningId, Long customerId, int count) {
        for (int i = 0; i < count; i++) {
            buyTicket(screeningId, customerId);
        }
    }

    @Transactional
    public void returnTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Билет не найден"));

        if (LocalDateTime.now().plusHours(1).isAfter(ticket.getScreening().getStartTime())) {
            throw new RuntimeException("Слишком поздно для возврата билета!");
        }
        ticketRepository.delete(ticket);
    }

    @Transactional
    public void cancelScreening(Long screeningId) {
        if (!screeningRepository.existsById(screeningId)) {
            throw new RuntimeException("Сеанс не найден");
        }
        screeningRepository.deleteById(screeningId);
    }

    public Screening createScreening(Screening screening) {
        if (screening.getPrice() <= 0) {
            throw new RuntimeException("Цена должна быть больше нуля!");
        }
        return screeningRepository.save(screening);
    }
}