package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findByUserIdAndActive(String userId, Boolean active);

    Optional<Subscription> findByIdAndUserId(String id, String userId);

    @Query("SELECT s FROM Subscription s WHERE s.active = true AND s.lastExecutionTime + (s.intervalSeconds * 1000) <= :currentTime")
    List<Subscription> findDueSubscriptions(@Param("currentTime") Long currentTime);

    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.user.id = :userId AND s.active = true")
    Double getTotalActiveSubscriptionAmount(@Param("userId") String userId);

    boolean existsByUserIdAndActive(String userId, Boolean active);

    @Query("SELECT s FROM Subscription s WHERE s.active = true AND s.user.account.balance < s.amount")
    List<Subscription> findSubscriptionsWithInsufficientBalance();
}