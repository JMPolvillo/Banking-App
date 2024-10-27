package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Exceptions.MarketApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {

    private final RestTemplate restTemplate;
    private static final String MARKET_API_URL = "https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-e0f31110-7521-4cb9-86a2-645f66eefb63/default/market-prices-simulator";

    public Map<String, Double> getAllPrices() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(MARKET_API_URL, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new MarketApiException("Failed to fetch market prices");
            }

            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching market prices: {}", e.getMessage());
            throw new MarketApiException(e.getMessage());
        }
    }

    public Double getPriceForAsset(String symbol) {
        try {
            Map<String, Double> prices = getAllPrices();

            if (!prices.containsKey(symbol)) {
                throw new MarketApiException("Price not available for asset: " + symbol);
            }

            return prices.get(symbol);
        } catch (Exception e) {
            log.error("Error fetching price for asset {}: {}", symbol, e.getMessage());
            throw new MarketApiException(e.getMessage());
        }
    }

    public boolean isValidAsset(String symbol) {
        try {
            Map<String, Double> prices = getAllPrices();
            return prices.containsKey(symbol);
        } catch (Exception e) {
            log.error("Error validating asset {}: {}", symbol, e.getMessage());
            throw new MarketApiException(e.getMessage());
        }
    }

    public Double calculateAssetQuantity(Double investmentAmount, String symbol) {
        Double currentPrice = getPriceForAsset(symbol);
        if (currentPrice <= 0) {
            throw new MarketApiException("Invalid price received for asset: " + symbol);
        }
        return investmentAmount / currentPrice;
    }

    public Double calculateInvestmentValue(Double quantity, String symbol) {
        Double currentPrice = getPriceForAsset(symbol);
        return quantity * currentPrice;
    }
}