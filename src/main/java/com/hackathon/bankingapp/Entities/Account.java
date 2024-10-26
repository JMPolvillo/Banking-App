package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private Double balance = 0.0;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private User user;
}
