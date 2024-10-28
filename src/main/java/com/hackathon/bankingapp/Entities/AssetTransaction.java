package com.hackathon.bankingapp.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "asset_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonBackReference(value = "user-asset-transactions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_asset_id")
    @JsonBackReference(value = "asset-transactions")
    private UserAsset userAsset;

    @NotBlank(message = "Symbol is required")
    @Column(nullable = false)
    private String symbol;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Double quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Total amount is required")
    @Column(nullable = false)
    private Double totalAmount;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Long transactionDate;

    @Column(nullable = false)
    private Double profitLoss;

    @PrePersist
    protected void onCreate() {
        transactionDate = System.currentTimeMillis();
        if (totalAmount == null && price != null && quantity != null) {
            totalAmount = price * quantity;
        }
    }
}