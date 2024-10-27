package com.hackathon.bankingapp.Exceptions;

public class InsufficientAssetException extends RuntimeException {
    public InsufficientAssetException(String symbol) {
        super("Insufficient " + symbol + " balance");
    }
}
