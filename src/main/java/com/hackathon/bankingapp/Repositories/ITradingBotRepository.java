package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.TradingBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITradingBotRepository extends JpaRepository<TradingBot, String> {

    Optional<TradingBot> findByUserId(String userId);

    List<TradingBot> findByActive(Boolean active);

    @Query("SELECT tb FROM TradingBot tb WHERE tb.active = true AND tb.lastCheckTime <= :checkTime")
    List<TradingBot> findBotsForUpdate(@Param("checkTime") Long checkTime);

    @Query("SELECT CASE WHEN COUNT(tb) > 0 THEN true ELSE false END FROM TradingBot tb WHERE tb.user.id = :userId AND tb.active = true")
    boolean hasActiveBot(@Param("userId") String userId);

    @Query("SELECT tb FROM TradingBot tb WHERE tb.active = true AND tb.user.account.balance >= tb.investmentAmount")
    List<TradingBot> findBotsWithSufficientBalance();

    @Query("SELECT COUNT(tb) FROM TradingBot tb WHERE tb.user.id = :userId")
    int countBotsByUser(@Param("userId") String userId);
}