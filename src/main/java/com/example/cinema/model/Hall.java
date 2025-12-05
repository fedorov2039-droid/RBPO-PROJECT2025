package com.example.cinema.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int capacity;
}