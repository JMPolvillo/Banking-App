package com.hackathon.bankingapp.DTO;

import lombok.Data;

public class UserDTO {

    @Data
    public static class RegistrationRequest {
        private String name;
        private String password;
        private String email;
        private String address;
        private String phoneNumber;
    }

    @Data
    public static class LoginRequest {
        private String identifier;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
    }

    @Data
    public static class UserResponse {
        private String name;
        private String email;
        private String phoneNumber;
        private String address;
        private String accountNumber;
        private String hashedPassword;
    }

    @Data
    public static class AccountResponse {
        private String accountNumber;
        private Double balance;
    }
}