    package com.example.cinema.model;
    
    import jakarta.persistence.*;
    
    @Entity
    @Table(name = "tickets")
    public class Ticket {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        @ManyToOne
        @JoinColumn(name = "screening_id")
        private Screening screening;
    
        @ManyToOne
        @JoinColumn(name = "customer_id")
        private Customer customer;
    
        private boolean refunded = false;
    
        public Ticket() {}
    
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    
        public Screening getScreening() { return screening; }
        public void setScreening(Screening screening) { this.screening = screening; }
    
        public Customer getCustomer() { return customer; }
        public void setCustomer(Customer customer) { this.customer = customer; }
    
        public boolean isRefunded() { return refunded; }
        public void setRefunded(boolean refunded) { this.refunded = refunded; }
    }