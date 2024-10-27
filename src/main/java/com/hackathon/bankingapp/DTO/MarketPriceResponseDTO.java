package com.hackathon.bankingapp.DTO;

import lombok.Data;
import java.util.Map;

@Data
public class MarketPriceResponseDTO {
    private Map<String, Double> prices;
}