package com.hackathon.bankingapp.Exceptions;

public class OtpAlreadyExistsException extends RuntimeException {
    public OtpAlreadyExistsException() {
        super("An active OTP already exists for this user");
    }
}