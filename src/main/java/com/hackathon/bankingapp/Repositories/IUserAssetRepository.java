package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.UserAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Repository
public interface IUserAssetRepository extends JpaRepository<UserAsset, String> {

    List<UserAsset> findByUserId(String userId);

    Optional<UserAsset> findByUserIdAndSymbol(String userId, String symbol);

    @Query("SELECT ua.symbol, ua.quantity FROM UserAsset ua WHERE ua.user.id = :userId")
    Map<String, Double> findUserAssetBalances(@Param("userId") String userId);

    @Query("SELECT SUM(ua.quantity * :currentPrice) FROM UserAsset ua WHERE ua.user.id = :userId AND ua.symbol = :symbol")
    Double calculateAssetValue(
            @Param("userId") String userId,
            @Param("symbol") String symbol,
            @Param("currentPrice") Double currentPrice
    );

    @Query("SELECT CASE WHEN COUNT(ua) > 0 THEN true ELSE false END FROM UserAsset ua WHERE ua.user.id = :userId AND ua.symbol = :symbol AND ua.quantity >= :quantity")
    boolean hasEnoughAssets(
            @Param("userId") String userId,
            @Param("symbol") String symbol,
            @Param("quantity") Double quantity
    );

    @Query("SELECT NEW map(ua.symbol as symbol, ua.quantity as quantity, ua.purchasePrice as purchasePrice) FROM UserAsset ua WHERE ua.user.id = :userId")
    List<Map<String, Object>> getUserPortfolio(@Param("userId") String userId);
}