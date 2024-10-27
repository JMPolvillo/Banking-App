package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateSubscriptionDTO {
    @NotBlank(message = "PIN is required")
    private String pin;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Interval is required")
    @Positive(message = "Interval must be positive")
    private Integer intervalSeconds;
}