package com.hackathon.bankingapp.Exceptions;

public class JwtTokenExpiredException extends RuntimeException {
    public JwtTokenExpiredException() {
        super("JWT token has expired");
    }
}