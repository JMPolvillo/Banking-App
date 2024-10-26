package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.OtpRequestDTO;
import com.hackathon.bankingapp.DTO.OtpVerificationDTO;
import com.hackathon.bankingapp.DTO.PasswordResetDTO;
import com.hackathon.bankingapp.Entities.OTP;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.IOTPRepository;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final IOTPRepository otpRepository;
    private final IUserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 3;
    private static final int COOLDOWN_MINUTES = 15;
    private static final int MAX_DAILY_OTP_REQUESTS = 5;

    @Transactional
    public String sendOTP(OtpRequestDTO request) {
        User user = userRepository.findByEmailOrAccountNumber(request.getIdentifier())
                .orElseThrow(() -> new UserNotFoundException(request.getIdentifier()));

        // Check daily OTP request limit
        int dailyRequests = otpRepository.countByIdentifierAndCreatedAtAfter(
                request.getIdentifier(),
                LocalDateTime.now().minusHours(24)
        );
        if (dailyRequests >= MAX_DAILY_OTP_REQUESTS) {
            throw new OtpLimitExceededException("Daily OTP request limit exceeded. Try again tomorrow.");
        }

        // Check if user is in cooldown period
        LocalDateTime lastFailedAttempt = otpRepository.findLastFailedAttemptTime(request.getIdentifier());
        if (lastFailedAttempt != null &&
                lastFailedAttempt.plusMinutes(COOLDOWN_MINUTES).isAfter(LocalDateTime.now())) {
            throw new OtpCooldownException("Too many failed attempts. Please wait before requesting a new OTP.");
        }

        // Check for existing active OTP
        if (otpRepository.existsByIdentifierAndUsedFalse(request.getIdentifier())) {
            throw new OtpAlreadyExistsException();
        }

        String otpCode = generateSecureOTP();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OTP otp = new OTP();
        otp.setIdentifier(request.getIdentifier());
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(expiryTime);
        otp.setUsed(false);
        otp.setAttempts(0);
        otp.setCreatedAt(LocalDateTime.now());
        otpRepository.save(otp);

        try {
            emailService.sendOtpEmail(user.getEmail(), otpCode);
        } catch (Exception e) {
            throw new EmailSendException(e.getMessage());
        }

        return "OTP sent successfully to: " + user.getEmail();
    }

    @Transactional
    public String verifyOTP(OtpVerificationDTO request) {
        OTP otp = otpRepository.findByIdentifierAndUsedFalse(request.getIdentifier())
                .orElseThrow(() -> new OtpInvalidException());

        // Check attempts
        if (otp.getAttempts() >= MAX_OTP_ATTEMPTS) {
            otp.setUsed(true);
            otpRepository.save(otp);
            throw new OtpMaxAttemptsExceededException();
        }

        // Verify OTP
        if (!otp.getOtpCode().equals(request.getOtp())) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);
            throw new OtpInvalidException();
        }

        if (otp.isExpired()) {
            throw new OtpExpiredException();
        }

        String resetToken = generateSecureToken();
        otp.setResetToken(resetToken);
        otp.setUsed(true);
        otpRepository.save(otp);

        return resetToken;
    }

    @Transactional
    public String resetPassword(PasswordResetDTO request) {
        validatePassword(request.getNewPassword());

        User user = userRepository.findByEmailOrAccountNumber(request.getIdentifier())
                .orElseThrow(() -> new UserNotFoundException(request.getIdentifier()));

        if (passwordEncoder.matches(request.getNewPassword(), user.getHashedPassword())) {
            throw new PasswordValidationException("New password must be different from current password");
        }

        OTP otp = otpRepository.findByIdentifierAndResetToken(
                        request.getIdentifier(), request.getResetToken())
                .orElseThrow(() -> new ResetTokenInvalidException());

        if (otp.isExpired()) {
            throw new ResetTokenExpiredException();
        }

        if (otp.isUsed()) {
            throw new OtpUsedException();
        }

        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otp.setUsed(true);
        otpRepository.save(otp);

        try {
            emailService.sendPasswordResetConfirmation(user.getEmail());
        } catch (Exception e) {
            throw new EmailSendException(e.getMessage());
        }

        return "Password reset successfully";
    }

    private String generateSecureOTP() {
        try {
            Random random = new Random();
            StringBuilder otp = new StringBuilder();
            for (int i = 0; i < OTP_LENGTH; i++) {
                otp.append(random.nextInt(10));
            }
            return otp.toString();
        } catch (Exception e) {
            throw new OtpGenerationException();
        }
    }

    private String generateSecureToken() {
        return UUID.randomUUID().toString();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new PasswordValidationException("Password must be at least 8 characters long");
        }
        if (password.length() > 128) {
            throw new PasswordValidationException("Password must be less than 128 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new PasswordValidationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new PasswordValidationException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new PasswordValidationException("Password must contain at least one special character");
        }
        if (password.contains(" ")) {
            throw new PasswordValidationException("Password cannot contain whitespace");
        }
    }
}