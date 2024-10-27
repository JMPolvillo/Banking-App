// Repositories/ITransactionRepository.java
package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccountNumberOrderByTransactionDateDesc(String accountNumber);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountNumber = :accountNumber OR t.targetAccountNumber = :accountNumber ORDER BY t.transactionDate DESC")
    List<Transaction> findAllAccountTransactions(@Param("accountNumber") String accountNumber);

    List<Transaction> findBySourceAccountNumberAndTransactionType(String accountNumber, TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountNumber = :accountNumber AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsInDateRange(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate
    );

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sourceAccountNumber = :accountNumber AND t.transactionType = 'CASH_DEPOSIT'")
    Double getTotalDeposits(@Param("accountNumber") String accountNumber);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sourceAccountNumber = :accountNumber AND t.transactionType = 'CASH_WITHDRAWAL'")
    Double getTotalWithdrawals(@Param("accountNumber") String accountNumber);

    Optional<Transaction> findFirstBySourceAccountNumberAndTransactionTypeOrderByTransactionDateDesc(
            String accountNumber,
            TransactionType type
    );

    @Query("SELECT t FROM Transaction t WHERE t.transactionType IN :types AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsForReport(
            @Param("types") List<TransactionType> types,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate
    );
}
