package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.CreatePinDTO;
import com.hackathon.bankingapp.DTO.UpdatePinDTO;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PinService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createPin(String accountNumber, CreatePinDTO dto) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        if (!passwordEncoder.matches(dto.getPassword(), user.getHashedPassword())) {
            throw new BadCredentialsException();
        }

        if (user.getPin() != null) {
            throw new PinAlreadyExistsException();
        }

        validatePin(dto.getPin());
        user.setPin(dto.getPin());
        userRepository.save(user);

        return "PIN created successfully";
    }

    @Transactional
    public String updatePin(String accountNumber, UpdatePinDTO dto) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        if (!passwordEncoder.matches(dto.getPassword(), user.getHashedPassword())) {
            throw new BadCredentialsException();
        }

        if (!dto.getOldPin().equals(user.getPin())) {
            throw new InvalidPinException();
        }

        validatePin(dto.getNewPin());
        user.setPin(dto.getNewPin());
        userRepository.save(user);

        return "PIN updated successfully";
    }

    private void validatePin(String pin) {
        if (pin == null || !pin.matches("^\\d{4}$")) {
            throw new PinValidationException("PIN must be exactly 4 digits");
        }
    }
}
