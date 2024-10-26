package com.hackathon.bankingapp.Exceptions;

public class PinValidationException extends RuntimeException {
    public PinValidationException(String message) {
        super(message);
    }
}
