package com.hackathon.bankingapp.Exceptions;

public class OtpGenerationException extends RuntimeException {
    public OtpGenerationException() {
        super("Error generating OTP");
    }
}
