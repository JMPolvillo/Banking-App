package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.AssetTransaction;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAssetTransactionRepository extends JpaRepository<AssetTransaction, String> {

    List<AssetTransaction> findByUserIdOrderByTransactionDateDesc(String userId);

    List<AssetTransaction> findByUserIdAndSymbolOrderByTransactionDateDesc(String userId, String symbol);

    List<AssetTransaction> findByUserIdAndTransactionTypeOrderByTransactionDateDesc(
            String userId,
            TransactionType transactionType
    );

    @Query("SELECT at FROM AssetTransaction at WHERE at.user.id = :userId AND at.transactionDate BETWEEN :startDate AND :endDate")
    List<AssetTransaction> findTransactionsInDateRange(
            @Param("userId") String userId,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate
    );

    @Query("SELECT SUM(at.profitLoss) FROM AssetTransaction at WHERE at.user.id = :userId AND at.symbol = :symbol")
    Double calculateTotalProfitLoss(
            @Param("userId") String userId,
            @Param("symbol") String symbol
    );

    @Query("SELECT AVG(at.price) FROM AssetTransaction at WHERE at.user.id = :userId AND at.symbol = :symbol AND at.transactionType = 'ASSET_PURCHASE'")
    Double getAveragePurchasePrice(
            @Param("userId") String userId,
            @Param("symbol") String symbol
    );

    @Query("SELECT SUM(at.totalAmount) FROM AssetTransaction at WHERE at.user.id = :userId AND at.transactionType = :transactionType")
    Double calculateTotalTransactionAmount(
            @Param("userId") String userId,
            @Param("transactionType") TransactionType transactionType
    );
}