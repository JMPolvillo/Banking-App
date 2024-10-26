package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.CreatePinDTO;
import com.hackathon.bankingapp.DTO.CreatePinForOtherDTO;
import com.hackathon.bankingapp.DTO.UpdatePinDTO;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Entities.PinAttempt;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import com.hackathon.bankingapp.Repositories.IPinAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinService {

    private final IUserRepository userRepository;
    private final IPinAttemptRepository pinAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int MAX_PIN_ATTEMPTS = 3;
    private static final int COOLDOWN_MINUTES = 15;
    private static final List<String> COMMON_PINS = List.of(
            "0000", "1234", "4321", "1111", "2222", "3333", "4444", "5555",
            "6666", "7777", "8888", "9999", "1212", "2580"
    );

    @Transactional
    public String createPin(String accountNumber, CreatePinDTO dto) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        checkAccountLock(accountNumber);

        if (!passwordEncoder.matches(dto.getPassword(), user.getHashedPassword())) {
            recordFailedAttempt(accountNumber, "PIN_CREATION");
            throw new BadCredentialsException();
        }

        if (user.getPin() != null) {
            throw new PinAlreadyExistsException();
        }

        validatePin(dto.getPin(), user);

        user.setPin(passwordEncoder.encode(dto.getPin()));
        userRepository.save(user);

        clearFailedAttempts(accountNumber);
        log.info("PIN created successfully for account: {}", accountNumber);

        return "PIN created successfully";
    }

    @Transactional
    public String updatePin(String accountNumber, UpdatePinDTO dto) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        checkAccountLock(accountNumber);

        if (!passwordEncoder.matches(dto.getPassword(), user.getHashedPassword())) {
            recordFailedAttempt(accountNumber, "PIN_UPDATE");
            throw new BadCredentialsException();
        }

        if (!passwordEncoder.matches(dto.getOldPin(), user.getPin())) {
            recordFailedAttempt(accountNumber, "PIN_VERIFICATION");
            throw new InvalidPinException();
        }

        validatePin(dto.getNewPin(), user);

        if (passwordEncoder.matches(dto.getNewPin(), user.getPin())) {
            throw new PinValidationException("New PIN must be different from current PIN");
        }

        user.setPin(passwordEncoder.encode(dto.getNewPin()));
        userRepository.save(user);

        clearFailedAttempts(accountNumber);
        log.info("PIN updated successfully for account: {}", accountNumber);

        return "PIN updated successfully";
    }

    private void validatePin(String pin, User user) {

        if (pin == null || !pin.matches("^\\d{4}$")) {
            throw new PinValidationException("PIN must be exactly 4 digits");
        }

        if (COMMON_PINS.contains(pin)) {
            throw new PinValidationException("PIN is too common. Please choose a more secure PIN");
        }

        if (pin.matches("(\\d)\\1{3}")) {
            throw new PinValidationException("PIN cannot contain four repeated digits");
        }

        if (isSequential(pin)) {
            throw new PinValidationException("PIN cannot be sequential numbers");
        }

        if (containsSensitiveData(pin, user)) {
            throw new PinValidationException("PIN cannot contain personal information");
        }
    }

    private boolean isSequential(String pin) {
        String forwards = "0123456789";
        String backwards = "9876543210";
        return forwards.contains(pin) || backwards.contains(pin);
    }

    private boolean containsSensitiveData(String pin, User user) {

        if (user.getAccountNumber() != null && user.getAccountNumber().contains(pin)) {
            return true;
        }
        return false;
    }

    private void checkAccountLock(String accountNumber) {
        LocalDateTime lockoutThreshold = LocalDateTime.now().minusMinutes(COOLDOWN_MINUTES);
        int recentAttempts = pinAttemptRepository.countRecentFailedAttempts(
                accountNumber,
                lockoutThreshold
        );

        if (recentAttempts >= MAX_PIN_ATTEMPTS) {
            throw new AccountLockedException(
                    "Account is temporarily locked due to too many failed attempts. " +
                            "Please try again after " + COOLDOWN_MINUTES + " minutes"
            );
        }
    }

    private void recordFailedAttempt(String accountNumber, String attemptType) {
        PinAttempt attempt = new PinAttempt();
        attempt.setAccountNumber(accountNumber);
        attempt.setAttemptType(attemptType);
        attempt.setAttemptTime(LocalDateTime.now());
        pinAttemptRepository.save(attempt);

        checkAccountLock(accountNumber);
    }

    private void clearFailedAttempts(String accountNumber) {
        pinAttemptRepository.deleteByAccountNumber(accountNumber);
    }

    @Transactional
    public String createPinForOtherAccount(String requesterAccountNumber, CreatePinForOtherDTO dto) {

        User requester = userRepository.findByAccountNumber(requesterAccountNumber)
                .orElseThrow(() -> new UserNotFoundException(requesterAccountNumber));

        if (!passwordEncoder.matches(dto.getPassword(), requester.getHashedPassword())) {
            recordFailedAttempt(requesterAccountNumber, "PIN_CREATION_OTHER");
            throw new BadCredentialsException();
        }

        User targetUser = userRepository.findByAccountNumber(dto.getTargetAccountNumber())
                .orElseThrow(() -> new UserNotFoundException(dto.getTargetAccountNumber()));

        checkAccountLock(dto.getTargetAccountNumber());

        if (targetUser.getPin() != null) {
            throw new PinAlreadyExistsException();
        }

        validatePin(dto.getPin(), targetUser);

        targetUser.setPin(passwordEncoder.encode(dto.getPin()));
        userRepository.save(targetUser);

        log.info("PIN created for account {} by account {}",
                dto.getTargetAccountNumber(), requesterAccountNumber);

        return "PIN created successfully for account: " + dto.getTargetAccountNumber();
    }
}