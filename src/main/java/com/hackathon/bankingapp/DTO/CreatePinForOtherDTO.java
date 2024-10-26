package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePinForOtherDTO {
    @NotBlank(message = "Target account number is required")
    private String targetAccountNumber;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits")
    private String pin;

    @NotBlank(message = "Password is required")
    private String password;
}
