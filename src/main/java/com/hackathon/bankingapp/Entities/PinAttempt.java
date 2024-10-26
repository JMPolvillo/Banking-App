package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "pin_attempts")
@Data
public class PinAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String attemptType;

    @Column(nullable = false)
    private LocalDateTime attemptTime;
}