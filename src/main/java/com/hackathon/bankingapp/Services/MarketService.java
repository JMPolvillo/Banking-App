package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Exceptions.MarketApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {

    private final RestTemplate restTemplate;
    private static final String MARKET_API_URL = "https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-e0f31110-7521-4cb9-86a2-645f66eefb63/default/market-prices-simulator";

    public Map<String, Double> getAllPrices() {
        try {
            log.debug("Fetching market prices from API...");
            ResponseEntity<Map> response = restTemplate.getForEntity(MARKET_API_URL, Map.class);
            log.debug("API Response: {}", response.getBody());

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
            log.debug("Getting price for asset: '{}'", symbol); // Added quotes to see any spaces
            Map<String, Double> prices = getAllPrices();
            log.debug("Available assets: {}", prices.keySet());

            if (symbol == null) {
                log.error("Asset symbol is null");
                throw new MarketApiException("Asset symbol cannot be null");
            }

            if (!prices.containsKey(symbol)) {
                log.error("Price not found for asset: '{}'", symbol);
                throw new MarketApiException("Price not available for asset: " + symbol);
            }

            Double price = prices.get(symbol);
            log.debug("Found price {} for asset '{}'", price, symbol);
            return price;
        } catch (Exception e) {
            log.error("Error fetching price for asset {}: {}", symbol, e.getMessage());
            throw new MarketApiException(e.getMessage());
        }
    }

    public boolean isValidAsset(String symbol) {
        try {
            log.debug("Validating asset: '{}'", symbol);
            if (symbol == null) {
                log.error("Asset symbol is null during validation");
                return false;
            }
            Map<String, Double> prices = getAllPrices();
            boolean isValid = prices.containsKey(symbol);
            log.debug("Asset '{}' validation result: {}", symbol, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Error validating asset {}: {}", symbol, e.getMessage());
            throw new MarketApiException(e.getMessage());
        }
    }

    public Double calculateAssetQuantity(Double investmentAmount, String symbol) {
        log.debug("Calculating quantity for investment amount {} in asset '{}'", investmentAmount, symbol);
        Double currentPrice = getPriceForAsset(symbol);
        if (currentPrice <= 0) {
            throw new MarketApiException("Invalid price received for asset: " + symbol);
        }
        Double quantity = investmentAmount / currentPrice;
        log.debug("Calculated quantity: {} for asset '{}'", quantity, symbol);
        return quantity;
    }

    public Double calculateInvestmentValue(Double quantity, String symbol) {
        log.debug("Calculating investment value for quantity {} of asset '{}'", quantity, symbol);
        Double currentPrice = getPriceForAsset(symbol);
        Double value = quantity * currentPrice;
        log.debug("Calculated value: {} for asset '{}'", value, symbol);
        return value;
    }
}