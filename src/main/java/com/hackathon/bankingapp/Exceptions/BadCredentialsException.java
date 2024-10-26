package com.hackathon.bankingapp.Exceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Bad credentials");
    }
}
