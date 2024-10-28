package com.hackathon.bankingapp.DTO;

import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class TransactionResponseDTO {
    private Long id;
    private Double amount;
    private TransactionType transactionType;
    private String transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;

    public static TransactionResponseDTO fromTransaction(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType());

        LocalDateTime date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(transaction.getTransactionDate()),
                ZoneId.systemDefault()
        );
        dto.setTransactionDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        dto.setSourceAccountNumber(transaction.getSourceAccountNumber());
        dto.setTargetAccountNumber(transaction.getTargetAccountNumber());
        return dto;
    }
}