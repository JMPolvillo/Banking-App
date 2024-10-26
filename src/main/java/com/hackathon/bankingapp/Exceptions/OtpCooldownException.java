package com.hackathon.bankingapp.Exceptions;

public class OtpCooldownException extends RuntimeException {
    public OtpCooldownException(String message) {
        super(message);
    }
}
