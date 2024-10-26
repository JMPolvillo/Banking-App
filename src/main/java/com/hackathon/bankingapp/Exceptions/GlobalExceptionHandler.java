package com.hackathon.bankingapp.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
  public ResponseEntity<String> handlePhoneNumberAlreadyExistsException(PhoneNumberAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  @ExceptionHandler(PasswordValidationException.class)
  public ResponseEntity<String> handlePasswordValidationException(PasswordValidationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(JwtTokenMissingException.class)
  public ResponseEntity<String> handleJwtTokenMissingException(JwtTokenMissingException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  @ExceptionHandler(JwtTokenExpiredException.class)
  public ResponseEntity<String> handleJwtTokenExpiredException(JwtTokenExpiredException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  @ExceptionHandler(JwtTokenInvalidException.class)
  public ResponseEntity<String> handleJwtTokenInvalidException(JwtTokenInvalidException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  @ExceptionHandler(JwtAuthenticationException.class)
  public ResponseEntity<String> handleJwtAuthenticationException(JwtAuthenticationException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  @ExceptionHandler(OtpException.class)
  public ResponseEntity<String> handleOtpException(OtpException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(OtpExpiredException.class)
  public ResponseEntity<String> handleOtpExpiredException(OtpExpiredException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(OtpInvalidException.class)
  public ResponseEntity<String> handleOtpInvalidException(OtpInvalidException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(ResetTokenException.class)
  public ResponseEntity<String> handleResetTokenException(ResetTokenException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(OtpGenerationException.class)
  public ResponseEntity<String> handleOtpGenerationException(OtpGenerationException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }

  @ExceptionHandler(OtpAlreadyExistsException.class)
  public ResponseEntity<String> handleOtpAlreadyExistsException(OtpAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(OtpUsedException.class)
  public ResponseEntity<String> handleOtpUsedException(OtpUsedException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(ResetTokenExpiredException.class)
  public ResponseEntity<String> handleResetTokenExpiredException(ResetTokenExpiredException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(ResetTokenInvalidException.class)
  public ResponseEntity<String> handleResetTokenInvalidException(ResetTokenInvalidException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(EmailSendException.class)
  public ResponseEntity<String> handleEmailSendException(EmailSendException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }

  @ExceptionHandler(OtpLimitExceededException.class)
  public ResponseEntity<String> handleOtpLimitExceededException(OtpLimitExceededException e) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
  }

  @ExceptionHandler(OtpCooldownException.class)
  public ResponseEntity<String> handleOtpCooldownException(OtpCooldownException e) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
  }

  @ExceptionHandler(OtpMaxAttemptsExceededException.class)
  public ResponseEntity<String> handleOtpMaxAttemptsExceededException(OtpMaxAttemptsExceededException e) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
  }

  @ExceptionHandler(PinValidationException.class)
  public ResponseEntity<String> handlePinValidationException(PinValidationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(InvalidPinException.class)
  public ResponseEntity<String> handleInvalidPinException(InvalidPinException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(PinAlreadyExistsException.class)
  public ResponseEntity<String> handlePinAlreadyExistsException(PinAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }
}