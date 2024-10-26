package com.hackathon.bankingapp.Exceptions;

public class OtpInvalidException extends RuntimeException {
    public OtpInvalidException() {
        super("Invalid OTP");
    }
}