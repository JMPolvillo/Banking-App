package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
}