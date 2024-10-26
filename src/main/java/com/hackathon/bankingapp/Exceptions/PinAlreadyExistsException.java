package com.hackathon.bankingapp.Exceptions;

public class PinAlreadyExistsException extends RuntimeException {
    public PinAlreadyExistsException() {
        super("PIN already exists for this account");
    }
}