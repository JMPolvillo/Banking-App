package com.hackathon.bankingapp.Exceptions;

public class UnauthorizedPinCreationException extends RuntimeException {
    public UnauthorizedPinCreationException() {
        super("You are not authorized to create PINs for other accounts");
    }
}
