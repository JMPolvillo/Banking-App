package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Exceptions.MarketApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {

    private final RestTemplate restTemplate;
    private static final String MARKET_API_URL = "https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-e0f31110-7521-4cb9-86a2-645f66eefb63/default/market-prices-simulator";

    private final ThreadLocal<Map<String, Double>> priceCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private Map<String, Double> fetchPricesFromApi() {
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

    public Map<String, Double> getAllPrices() {
        Map<String, Double> cache = priceCache.get();
        if (!cache.isEmpty()) {
            return cache;
        }

        Map<String, Double> prices = fetchPricesFromApi();
        cache.putAll(prices);
        return cache;
    }

    public Double getPriceForAsset(String symbol) {
        if (symbol == null) {
            log.error("Asset symbol is null");
            throw new MarketApiException("Asset symbol cannot be null");
        }

        Map<String, Double> cache = priceCache.get();
        Double cachedPrice = cache.get(symbol);

        if (cachedPrice != null) {
            log.debug("Using cached price {} for asset '{}'", cachedPrice, symbol);
            return cachedPrice;
        }

        Map<String, Double> prices = getAllPrices();
        Double price = prices.get(symbol);

        if (price == null) {
            log.error("Price not found for asset: '{}'", symbol);
            throw new MarketApiException("Price not available for asset: " + symbol);
        }

        log.debug("Found price {} for asset '{}'", price, symbol);
        return price;
    }

    public boolean isValidAsset(String symbol) {
        if (symbol == null) {
            log.error("Asset symbol is null during validation");
            return false;
        }

        try {
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
        if (symbol == null) {
            throw new MarketApiException("Asset symbol cannot be null");
        }
        if (investmentAmount == null || investmentAmount <= 0) {
            throw new MarketApiException("Investment amount must be positive");
        }

        log.debug("Calculating quantity for investment amount {} in asset '{}'", investmentAmount, symbol);
        Double currentPrice = getPriceForAsset(symbol);
        Double quantity = investmentAmount / currentPrice;
        log.debug("Calculated quantity: {} for asset '{}'", quantity, symbol);
        return quantity;
    }

    public Double calculateInvestmentValue(Double quantity, String symbol) {
        if (symbol == null) {
            throw new MarketApiException("Asset symbol cannot be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new MarketApiException("Quantity must be positive");
        }

        log.debug("Calculating investment value for quantity {} of asset '{}'", quantity, symbol);
        Double currentPrice = getPriceForAsset(symbol);
        Double value = quantity * currentPrice;
        log.debug("Calculated value: {} for asset '{}'", value, symbol);
        return value;
    }

    public void finishTransaction() {
        priceCache.get().clear();
    }
}