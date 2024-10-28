package com.hackathon.bankingapp.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Long transactionDate;

    @Column(nullable = false)
    private String sourceAccountNumber;

    private String targetAccountNumber;

    @JsonBackReference(value = "user-source-transactions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id")
    private User sourceUser;

    @JsonBackReference(value = "user-target-transactions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @PrePersist
    protected void onCreate() {
        transactionDate = System.currentTimeMillis();
    }
}