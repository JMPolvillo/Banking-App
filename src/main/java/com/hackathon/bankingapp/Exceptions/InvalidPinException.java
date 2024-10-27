package com.hackathon.bankingapp.Exceptions;

public class InvalidPinException extends RuntimeException {
    public InvalidPinException() {
        super("Invalid PIN");
    }

    public InvalidPinException(String message) {
        super(message);
    }
}