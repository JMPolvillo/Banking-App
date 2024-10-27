package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.CreateSubscriptionDTO;
import com.hackathon.bankingapp.Services.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-actions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> createSubscription(
            @Valid @RequestBody CreateSubscriptionDTO dto,
            Authentication authentication) {
        String message = subscriptionService.createSubscription(authentication.getName(), dto);
        return ResponseEntity.ok(Map.of("msg", message));
    }
}