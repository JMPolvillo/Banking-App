package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.AutoInvestDTO;
import com.hackathon.bankingapp.Services.TradingBotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-actions")
@RequiredArgsConstructor
public class TradingBotController {

    private final TradingBotService tradingBotService;

    @PostMapping("/enable-auto-invest")
    public ResponseEntity<Map<String, String>> enableAutoInvest(
            @Valid @RequestBody AutoInvestDTO dto,
            Authentication authentication) {
        String message = tradingBotService.enableAutoInvest(authentication.getName(), dto);
        return ResponseEntity.ok(Map.of("msg", message));
    }
}