package com.hackathon.bankingapp.Exceptions;

public class OtpLimitExceededException extends RuntimeException {
    public OtpLimitExceededException(String message) {
        super(message);
    }
}
