package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.CreatePinDTO;
import com.hackathon.bankingapp.DTO.UpdatePinDTO;
import com.hackathon.bankingapp.Services.PinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pin")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPin(
            @Valid @RequestBody CreatePinDTO dto,
            Authentication authentication) {
        String message = pinService.createPin(authentication.getName(), dto);
        return ResponseEntity.ok(Map.of("msg", message));
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updatePin(
            @Valid @RequestBody UpdatePinDTO dto,
            Authentication authentication) {
        String message = pinService.updatePin(authentication.getName(), dto);
        return ResponseEntity.ok(Map.of("msg", message));
    }
}