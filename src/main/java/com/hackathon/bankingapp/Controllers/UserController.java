package com.hackathon.bankingapp.Controllers;


import com.hackathon.bankingapp.DTO.*;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO dto) {
        try {
            User user = userService.registerUser(dto);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        try {
            String token = userService.login(dto);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Bad credentials");
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        try {
            User user = userService.getUserInfo(authentication.getName());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/account-info")
    public ResponseEntity<?> getAccountInfo(Authentication authentication) {
        try {
            AccountInfoDTO accountInfo = userService.getAccountInfo(authentication.getName());
            return ResponseEntity.ok(accountInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT tokens are stateless, so we don't need to do anything server-side
        return ResponseEntity.ok("Logged out successfully");
    }
}
