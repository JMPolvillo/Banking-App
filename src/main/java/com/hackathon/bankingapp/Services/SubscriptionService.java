package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.CreateSubscriptionDTO;
import com.hackathon.bankingapp.Entities.*;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.ISubscriptionRepository;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final ISubscriptionRepository subscriptionRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createSubscription(String accountNumber, CreateSubscriptionDTO dto) {

        User user = validateUserAndPin(accountNumber, dto.getPin());

        if (user.getAccount().getBalance() < dto.getAmount()) {
            throw new InsufficientBalanceException();
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setAmount(dto.getAmount());
        subscription.setIntervalSeconds(dto.getIntervalSeconds());
        subscription.setActive(true);
        subscription.setLastExecutionTime(System.currentTimeMillis());

        subscriptionRepository.save(subscription);
        log.info("Subscription created for user: {}", accountNumber);

        return "Subscription created successfully.";
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processSubscriptions() {
        Long currentTime = System.currentTimeMillis();
        List<Subscription> dueSubscriptions = subscriptionRepository.findDueSubscriptions(currentTime);

        for (Subscription subscription : dueSubscriptions) {
            try {
                processSubscriptionPayment(subscription);
            } catch (Exception e) {
                log.error("Error processing subscription {}: {}", subscription.getId(), e.getMessage());
                subscription.setActive(false);
                subscriptionRepository.save(subscription);
            }
        }
    }

    private void processSubscriptionPayment(Subscription subscription) {
        User user = subscription.getUser();
        Double balance = user.getAccount().getBalance();

        if (balance < subscription.getAmount()) {
            log.warn("Insufficient balance for subscription: {}", subscription.getId());
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
            throw new InsufficientBalanceException();
        }

        user.getAccount().setBalance(balance - subscription.getAmount());
        subscription.setLastExecutionTime(System.currentTimeMillis());

        Transaction transaction = new Transaction();
        transaction.setAmount(subscription.getAmount());
        transaction.setTransactionType(TransactionType.SUBSCRIPTION);
        transaction.setSourceAccountNumber(user.getAccountNumber());
        transaction.setSourceUser(user);
        transaction.setTransactionDate(System.currentTimeMillis());

        userRepository.save(user);
        subscriptionRepository.save(subscription);
    }

    private User validateUserAndPin(String accountNumber, String pin) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        if (user.getPin() == null) {
            throw new InvalidPinException("PIN not set for this account");
        }

        if (!passwordEncoder.matches(pin, user.getPin())) {
            throw new InvalidPinException();
        }

        return user;
    }
}