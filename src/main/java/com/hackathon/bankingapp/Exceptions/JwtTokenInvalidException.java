package com.hackathon.bankingapp.Exceptions;

public class JwtTokenInvalidException extends RuntimeException {
  public JwtTokenInvalidException() {
    super("JWT token is invalid");
  }
}