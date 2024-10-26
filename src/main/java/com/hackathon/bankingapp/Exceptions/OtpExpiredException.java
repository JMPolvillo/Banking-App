package com.hackathon.bankingapp.Exceptions;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException() {
        super("OTP has expired");
    }
}