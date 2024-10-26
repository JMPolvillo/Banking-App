package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.PinAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface IPinAttemptRepository extends JpaRepository<PinAttempt, String> {

    @Query("SELECT COUNT(pa) FROM PinAttempt pa WHERE pa.accountNumber = :accountNumber " +
            "AND pa.attemptTime > :since")
    int countRecentFailedAttempts(String accountNumber, LocalDateTime since);

    void deleteByAccountNumber(String accountNumber);
}