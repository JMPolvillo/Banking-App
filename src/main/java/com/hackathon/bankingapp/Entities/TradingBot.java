package com.hackathon.bankingapp.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "trading_bots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradingBot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonBackReference(value = "user-trading-bot")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean active = false;

    @Column(nullable = false)
    private Long lastCheckTime;

    @ElementCollection
    @CollectionTable(name = "bot_asset_prices")
    @MapKeyColumn(name = "asset_symbol")
    @Column(name = "last_price")
    private Map<String, Double> lastPrices = new HashMap<>();

    @Column(nullable = false)
    private Double buyThreshold = 20.0;

    @Column(nullable = false)
    private Double sellThreshold = 20.0;

    @Column(nullable = false)
    private Double investmentAmount = 100.0;

    @Column(nullable = false)
    private Long createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        lastCheckTime = System.currentTimeMillis();
    }
}