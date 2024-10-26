package com.hackathon.bankingapp.Exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String identifier) {
        super("User not found for the given identifier: " + identifier);
    }
}
