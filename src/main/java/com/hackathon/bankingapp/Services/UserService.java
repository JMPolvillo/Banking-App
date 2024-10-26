package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.*;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Entities.Account;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User registerUser(UserRegistrationDTO dto) {
        validateRegistrationData(dto);

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setHashedPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAccountNumber(generateAccountNumber());

        Account account = new Account();
        account.setAccountNumber(user.getAccountNumber());
        account.setBalance(0.0);
        account.setUser(user);
        user.setAccount(account);

        return userRepository.save(user);
    }

    public String login(LoginDTO loginDTO) {
        try {
            if (!userRepository.existsByEmail(loginDTO.getIdentifier()) &&
                    !userRepository.existsByAccountNumber(loginDTO.getIdentifier())) {
                throw new UserNotFoundException(loginDTO.getIdentifier());
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getIdentifier(),
                            loginDTO.getPassword()
                    )
            );

            return jwtTokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException();
        }
    }

    public User getUserInfo(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));
    }

    public AccountInfoDTO getAccountInfo(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        AccountInfoDTO dto = new AccountInfoDTO();
        dto.setAccountNumber(user.getAccountNumber());
        dto.setBalance(user.getAccount().getBalance());
        return dto;
    }

    private void validateRegistrationData(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException();
        }
        validatePassword(dto.getPassword());
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

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}