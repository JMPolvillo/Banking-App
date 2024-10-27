package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AutoInvestDTO {
    @NotBlank(message = "PIN is required")
    private String pin;
}