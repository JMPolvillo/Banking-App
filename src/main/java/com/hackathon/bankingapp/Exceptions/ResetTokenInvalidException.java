package com.hackathon.bankingapp.Exceptions;

public class ResetTokenInvalidException extends RuntimeException {
  public ResetTokenInvalidException() {
    super("Invalid reset token");
  }
}