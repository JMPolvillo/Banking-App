package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SellAssetDTO {
    @NotBlank(message = "Asset symbol is required")
    private String assetSymbol;

    @NotBlank(message = "PIN is required")
    private String pin;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;
}