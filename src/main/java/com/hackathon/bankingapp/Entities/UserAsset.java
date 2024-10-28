package com.hackathon.bankingapp.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonBackReference(value = "user-assets")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double purchasePrice;

    @Column(nullable = false)
    private Long purchaseDate;

    @JsonManagedReference(value = "asset-transactions")
    @OneToMany(mappedBy = "userAsset")
    private List<AssetTransaction> transactions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        purchaseDate = System.currentTimeMillis();
    }
}