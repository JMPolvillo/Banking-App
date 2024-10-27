package com.hackathon.bankingapp.Exceptions;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(String symbol) {
        super("Asset not found: " + symbol);
    }
}
