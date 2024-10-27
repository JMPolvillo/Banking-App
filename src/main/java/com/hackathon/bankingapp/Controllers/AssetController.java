package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.*;
import com.hackathon.bankingapp.Services.AssetService;
import com.hackathon.bankingapp.Services.MarketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final MarketService marketService;

    @PostMapping("/buy-asset")
    public ResponseEntity<String> buyAsset(
            @Valid @RequestBody BuyAssetDTO dto,
            Authentication authentication) {
        log.info("Received buy asset request: {}", dto);  // Add this line
        String message = assetService.buyAsset(authentication.getName(), dto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/sell-asset")
    public ResponseEntity<String> sellAsset(
            @Valid @RequestBody SellAssetDTO dto,
            Authentication authentication) {
        String message = assetService.sellAsset(authentication.getName(), dto);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/net-worth")
    public ResponseEntity<Double> getNetWorth(Authentication authentication) {
        Double netWorth = assetService.calculateNetWorth(authentication.getName());
        return ResponseEntity.ok(netWorth);
    }

    @GetMapping("/assets")
    public ResponseEntity<Map<String, Double>> getUserAssets(Authentication authentication) {
        Map<String, Double> assets = assetService.getUserAssets(authentication.getName());
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/market/prices")
    public ResponseEntity<Map<String, Double>> getAllPrices() {
        return ResponseEntity.ok(marketService.getAllPrices());
    }

    @GetMapping("/market/prices/{symbol}")
    public ResponseEntity<Double> getAssetPrice(@PathVariable String symbol) {
        return ResponseEntity.ok(marketService.getPriceForAsset(symbol));
    }
}