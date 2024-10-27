package com.hackathon.bankingapp.Exceptions;

public class MarketApiException extends RuntimeException {
    public MarketApiException(String message) {
        super("Market API error: " + message);
    }
}
