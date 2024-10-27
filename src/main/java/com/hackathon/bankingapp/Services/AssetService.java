package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.BuyAssetDTO;
import com.hackathon.bankingapp.DTO.SellAssetDTO;
import com.hackathon.bankingapp.Entities.*;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.IUserAssetRepository;
import com.hackathon.bankingapp.Repositories.IAssetTransactionRepository;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final IUserRepository userRepository;
    private final IUserAssetRepository userAssetRepository;
    private final IAssetTransactionRepository assetTransactionRepository;
    private final MarketService marketService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String buyAsset(String accountNumber, BuyAssetDTO dto) {

        User user = validateUserAndPin(accountNumber, dto.getPin());

        if (!marketService.isValidAsset(dto.getAssetSymbol())) {
            throw new AssetNotFoundException(dto.getAssetSymbol());
        }
        Double currentPrice = marketService.getPriceForAsset(dto.getAssetSymbol());

        if (user.getAccount().getBalance() < dto.getAmount()) {
            throw new InsufficientBalanceException();
        }

        Double quantity = marketService.calculateAssetQuantity(dto.getAmount(), dto.getAssetSymbol());

        UserAsset userAsset = userAssetRepository
                .findByUserIdAndSymbol(user.getId(), dto.getAssetSymbol())
                .orElse(new UserAsset());

        if (userAsset.getId() == null) {
            userAsset.setUser(user);
            userAsset.setSymbol(dto.getAssetSymbol());
            userAsset.setQuantity(quantity);
            userAsset.setPurchasePrice(currentPrice);
        } else {

            Double totalQuantity = userAsset.getQuantity() + quantity;
            Double totalCost = (userAsset.getQuantity() * userAsset.getPurchasePrice()) + (quantity * currentPrice);
            userAsset.setQuantity(totalQuantity);
            userAsset.setPurchasePrice(totalCost / totalQuantity);
        }

        AssetTransaction transaction = new AssetTransaction();
        transaction.setUser(user);
        transaction.setSymbol(dto.getAssetSymbol());
        transaction.setQuantity(quantity);
        transaction.setPrice(currentPrice);
        transaction.setTotalAmount(dto.getAmount());
        transaction.setTransactionType(TransactionType.ASSET_PURCHASE);
        transaction.setProfitLoss(0.0);

        user.getAccount().setBalance(user.getAccount().getBalance() - dto.getAmount());

        userRepository.save(user);
        userAssetRepository.save(userAsset);
        assetTransactionRepository.save(transaction);

        sendPurchaseConfirmationEmail(user, dto.getAssetSymbol(), quantity, dto.getAmount(), userAsset.getQuantity());

        return "Asset purchase successful.";
    }

    @Transactional
    public String sellAsset(String accountNumber, SellAssetDTO dto) {

        User user = validateUserAndPin(accountNumber, dto.getPin());

        if (!marketService.isValidAsset(dto.getAssetSymbol())) {
            throw new AssetNotFoundException(dto.getAssetSymbol());
        }
        Double currentPrice = marketService.getPriceForAsset(dto.getAssetSymbol());

        UserAsset userAsset = userAssetRepository
                .findByUserIdAndSymbol(user.getId(), dto.getAssetSymbol())
                .orElseThrow(() -> new InsufficientAssetException(dto.getAssetSymbol()));

        if (userAsset.getQuantity() < dto.getQuantity()) {
            throw new InsufficientAssetException(dto.getAssetSymbol());
        }

        Double saleAmount = dto.getQuantity() * currentPrice;
        Double profitLoss = (currentPrice - userAsset.getPurchasePrice()) * dto.getQuantity();

        userAsset.setQuantity(userAsset.getQuantity() - dto.getQuantity());

        AssetTransaction transaction = new AssetTransaction();
        transaction.setUser(user);
        transaction.setSymbol(dto.getAssetSymbol());
        transaction.setQuantity(dto.getQuantity());
        transaction.setPrice(currentPrice);
        transaction.setTotalAmount(saleAmount);
        transaction.setTransactionType(TransactionType.ASSET_SELL);
        transaction.setProfitLoss(profitLoss);

        user.getAccount().setBalance(user.getAccount().getBalance() + saleAmount);

        userRepository.save(user);
        if (userAsset.getQuantity() > 0) {
            userAssetRepository.save(userAsset);
        } else {
            userAssetRepository.delete(userAsset);
        }
        assetTransactionRepository.save(transaction);

        sendSaleConfirmationEmail(user, dto.getAssetSymbol(), dto.getQuantity(), profitLoss, userAsset.getQuantity());

        return "Asset sale successful.";
    }

    public Double calculateNetWorth(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        Double cashBalance = user.getAccount().getBalance();
        Double assetValue = calculateTotalAssetValue(user.getId());

        return cashBalance + assetValue;
    }

    private Double calculateTotalAssetValue(String userId) {
        Map<String, Double> userAssets = userAssetRepository.findUserAssetBalances(userId);
        Double totalValue = 0.0;

        for (Map.Entry<String, Double> asset : userAssets.entrySet()) {
            Double currentPrice = marketService.getPriceForAsset(asset.getKey());
            totalValue += currentPrice * asset.getValue();
        }

        return totalValue;
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

    private void sendPurchaseConfirmationEmail(User user, String symbol, Double quantity, Double amount, Double totalHoldings) {
        emailService.sendInvestmentPurchaseConfirmation(
                user.getEmail(),
                user.getName(),
                symbol,
                quantity,
                amount,
                totalHoldings,
                marketService.getPriceForAsset(symbol),
                user.getAccount().getBalance(),
                calculateNetWorth(user.getAccountNumber())
        );
    }

    private void sendSaleConfirmationEmail(User user, String symbol, Double quantity, Double profitLoss, Double remainingHoldings) {
        emailService.sendInvestmentSaleConfirmation(
                user.getEmail(),
                user.getName(),
                symbol,
                quantity,
                profitLoss,
                remainingHoldings,
                marketService.getPriceForAsset(symbol),
                user.getAccount().getBalance(),
                calculateNetWorth(user.getAccountNumber())
        );
    }
}