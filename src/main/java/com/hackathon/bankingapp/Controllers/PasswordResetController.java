package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.OtpRequestDTO;
import com.hackathon.bankingapp.DTO.OtpVerificationDTO;
import com.hackathon.bankingapp.DTO.PasswordResetDTO;
import com.hackathon.bankingapp.Services.OTPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final OTPService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOTP(@Valid @RequestBody OtpRequestDTO request) {
        String message = otpService.sendOTP(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOTP(@Valid @RequestBody OtpVerificationDTO request) {
        String resetToken = otpService.verifyOTP(request);
        Map<String, String> response = new HashMap<>();
        response.put("passwordResetToken", resetToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
        String message = otpService.resetPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}