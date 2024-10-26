package com.hackathon.bankingapp.Exceptions;

public class ResetTokenExpiredException extends RuntimeException {
  public ResetTokenExpiredException() {
    super("Password reset token has expired");
  }
}