package com.hackathon.bankingapp.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @Column(nullable = false)
    private String hashedPassword;

    @Column(unique = true, nullable = false, length = 6)
    private String accountNumber;

    private String pin;

    @JsonManagedReference(value = "user-account")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @JsonManagedReference(value = "user-transactions")
    @OneToMany(mappedBy = "sourceUser", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @JsonManagedReference(value = "user-assets")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAsset> assets = new ArrayList<>();

    @JsonManagedReference(value = "user-asset-transactions")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AssetTransaction> assetTransactions = new ArrayList<>();

    @JsonManagedReference(value = "user-subscriptions")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions = new ArrayList<>();

    @JsonManagedReference(value = "user-trading-bot")
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TradingBot tradingBot;

}