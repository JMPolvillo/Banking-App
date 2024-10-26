package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.LoginDTO;
import com.hackathon.bankingapp.DTO.UserRegistrationDTO;
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
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDTO dto) {
        return ResponseEntity.ok(userService.registerUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    public ResponseEntity<User> getUserInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserInfo(authentication.getName()));
    }

    @GetMapping("/account-info")
    public ResponseEntity<?> getAccountInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getAccountInfo(authentication.getName()));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }
}