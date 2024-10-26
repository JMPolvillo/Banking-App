package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    private String phoneNumber;
}
