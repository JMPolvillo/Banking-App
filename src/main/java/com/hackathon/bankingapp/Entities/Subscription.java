package com.hackathon.bankingapp.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonBackReference(value = "user-subscriptions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    @Column(nullable = false)
    private Double amount;

    @NotNull(message = "Interval is required")
    @Min(value = 1, message = "Interval must be positive")
    @Column(nullable = false)
    private Integer intervalSeconds;

    @Column(nullable = false)
    private Long lastExecutionTime;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Long createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        lastExecutionTime = System.currentTimeMillis();
    }
}