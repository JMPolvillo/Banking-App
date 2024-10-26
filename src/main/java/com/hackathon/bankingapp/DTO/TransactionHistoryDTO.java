package com.hackathon.bankingapp.DTO;

import com.hackathon.bankingapp.Entities.TransactionType;
import lombok.Data;

@Data
public class TransactionHistoryDTO {
    private Long id;
    private Double amount;
    private TransactionType transactionType;
    private Long transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;
}