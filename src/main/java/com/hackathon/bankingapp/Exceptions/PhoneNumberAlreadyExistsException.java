package com.hackathon.bankingapp.Exceptions;

public class PhoneNumberAlreadyExistsException extends RuntimeException {
  public PhoneNumberAlreadyExistsException() {
    super("Phone number already exists");
  }
}