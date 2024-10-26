package com.hackathon.bankingapp.Exceptions;

public class EmailSendException extends RuntimeException {
    public EmailSendException(String message) {
        super("Failed to send email: " + message);
    }
}