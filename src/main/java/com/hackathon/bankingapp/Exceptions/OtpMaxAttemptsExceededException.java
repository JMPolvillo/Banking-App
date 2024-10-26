package com.hackathon.bankingapp.Exceptions;

public class OtpMaxAttemptsExceededException extends RuntimeException {
    public OtpMaxAttemptsExceededException() {
        super("Maximum OTP verification attempts exceeded");
    }
}
