package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.DepositDTO;
import com.hackathon.bankingapp.DTO.TransferDTO;
import com.hackathon.bankingapp.DTO.WithdrawDTO;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.TransactionType;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.ITransactionRepository;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final ITransactionRepository transactionRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String deposit(String accountNumber, DepositDTO depositDTO) {
        User user = validateUserAndPin(accountNumber, depositDTO.getPin());

        Transaction transaction = new Transaction();
        transaction.setAmount(depositDTO.getAmount());
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setSourceAccountNumber(accountNumber);
        transaction.setSourceUser(user);
        transaction.setTransactionDate(System.currentTimeMillis());
        transactionRepository.save(transaction);

        user.getAccount().setBalance(user.getAccount().getBalance() + depositDTO.getAmount());
        userRepository.save(user);

        return "Cash deposited successfully";
    }

    @Transactional
    public String withdraw(String accountNumber, WithdrawDTO withdrawDTO) {

        User user = validateUserAndPin(accountNumber, withdrawDTO.getPin());

        if (user.getAccount().getBalance() < withdrawDTO.getAmount()) {
            throw new InsufficientBalanceException();
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(withdrawDTO.getAmount());
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setSourceAccountNumber(accountNumber);
        transaction.setSourceUser(user);
        transaction.setTransactionDate(System.currentTimeMillis());
        transactionRepository.save(transaction);

        user.getAccount().setBalance(user.getAccount().getBalance() - withdrawDTO.getAmount());
        userRepository.save(user);

        return "Cash withdrawn successfully";
    }

    @Transactional
    public String transfer(String sourceAccountNumber, TransferDTO transferDTO) {

        User sourceUser = validateUserAndPin(sourceAccountNumber, transferDTO.getPin());

        User targetUser = userRepository.findByAccountNumber(transferDTO.getTargetAccountNumber())
                .orElseThrow(() -> new UserNotFoundException(transferDTO.getTargetAccountNumber()));

        if (sourceUser.getAccount().getBalance() < transferDTO.getAmount()) {
            throw new InsufficientBalanceException();
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transferDTO.getAmount());
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setSourceAccountNumber(sourceAccountNumber);
        transaction.setTargetAccountNumber(transferDTO.getTargetAccountNumber());
        transaction.setSourceUser(sourceUser);
        transaction.setTargetUser(targetUser);
        transaction.setTransactionDate(System.currentTimeMillis());
        transactionRepository.save(transaction);

        sourceUser.getAccount().setBalance(sourceUser.getAccount().getBalance() - transferDTO.getAmount());
        targetUser.getAccount().setBalance(targetUser.getAccount().getBalance() + transferDTO.getAmount());
        userRepository.save(sourceUser);
        userRepository.save(targetUser);

        return "Fund transferred successfully";
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {

        if (!userRepository.existsByAccountNumber(accountNumber)) {
            throw new UserNotFoundException(accountNumber);
        }

        return transactionRepository.findAllAccountTransactions(accountNumber);
    }

    private User validateUserAndPin(String accountNumber, String pin) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        if (user.getPin() == null) {
            throw new InvalidPinException();
        }

        if (!passwordEncoder.matches(pin, user.getPin())) {
            throw new InvalidPinException();
        }

        return user;
    }

    private void validateAmount(Double amount) {
        if (amount <= 0) {
            throw new InvalidTransactionException("Amount must be positive");
        }
    }

    private void validateBalance(User user, Double amount) {
        if (user.getAccount().getBalance() < amount) {
            throw new InsufficientBalanceException();
        }
    }

}