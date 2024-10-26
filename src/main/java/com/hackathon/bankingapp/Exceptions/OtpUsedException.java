package com.hackathon.bankingapp.Exceptions;

public class OtpUsedException extends RuntimeException {
  public OtpUsedException() {
    super("This OTP has already been used");
  }
}
