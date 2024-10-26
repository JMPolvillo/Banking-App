package com.hackathon.bankingapp.Exceptions;

public class JwtTokenMissingException extends RuntimeException {
  public JwtTokenMissingException() {
    super("JWT token is missing in request");
  }
}
