package com.example.cinema.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "tickets",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tickets_screening_seat",
                columnNames = {"screening_id", "seat_number"}
        )
)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private boolean refunded = false;

    public Ticket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Screening getScreening() { return screening; }
    public void setScreening(Screening screening) { this.screening = screening; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }

    public boolean isRefunded() { return refunded; }
    public void setRefunded(boolean refunded) { this.refunded = refunded; }
}
