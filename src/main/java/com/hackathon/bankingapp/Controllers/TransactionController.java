package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.DepositDTO;
import com.hackathon.bankingapp.DTO.TransactionResponseDTO;
import com.hackathon.bankingapp.DTO.TransferDTO;
import com.hackathon.bankingapp.DTO.WithdrawDTO;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(
            @Valid @RequestBody DepositDTO depositDTO,
            Authentication authentication) {
        String message = transactionService.deposit(authentication.getName(), depositDTO);
        return ResponseEntity.ok(Map.of("msg", message));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(
            @Valid @RequestBody WithdrawDTO withdrawDTO,
            Authentication authentication) {
        String message = transactionService.withdraw(authentication.getName(), withdrawDTO);
        return ResponseEntity.ok(Map.of("msg", message));
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @Valid @RequestBody TransferDTO transferDTO,
            Authentication authentication) {
        String message = transactionService.transfer(authentication.getName(), transferDTO);
        return ResponseEntity.ok(Map.of("msg", message));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(Authentication authentication) {
        List<Transaction> transactions = transactionService.getTransactionHistory(authentication.getName());
        List<TransactionResponseDTO> response = transactions.stream()
                .map(TransactionResponseDTO::fromTransaction)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}