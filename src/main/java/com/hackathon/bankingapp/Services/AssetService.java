package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.BuyAssetDTO;
import com.hackathon.bankingapp.DTO.SellAssetDTO;
import com.hackathon.bankingapp.Entities.*;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.IUserAssetRepository;
import com.hackathon.bankingapp.Repositories.IAssetTransactionRepository;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;
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

    private final ThreadLocal<PriceCache> priceCache = ThreadLocal.withInitial(PriceCache::new);

    private static class PriceCache {
        private String symbol;
        private Double price;

        void set(String symbol, Double price) {
            this.symbol = symbol;
            this.price = price;
        }

        Double getPrice(String symbol) {
            return (this.symbol != null && this.symbol.equals(symbol)) ? this.price : null;
        }

        void clear() {
            this.symbol = null;
            this.price = null;
        }
    }

    @Transactional
    public String buyAsset(String accountNumber, BuyAssetDTO dto) {
        try {
            User user = validateUserAndPin(accountNumber, dto.getPin());

            if (!marketService.isValidAsset(dto.getAssetSymbol())) {
                throw new AssetNotFoundException(dto.getAssetSymbol());
            }
            Double currentPrice = marketService.getPriceForAsset(dto.getAssetSymbol());
            priceCache.get().set(dto.getAssetSymbol(), currentPrice);

            if (user.getAccount().getBalance() < dto.getAmount()) {
                throw new InsufficientBalanceException();
            }

            Double quantity = dto.getAmount() / currentPrice; // Calculate directly to avoid extra API call

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

            // Calculate net worth before sending email
            Double netWorth = calculateTotalAssetValue(user.getId()) + user.getAccount().getBalance();

            try {
                emailService.sendInvestmentPurchaseConfirmation(
                        user.getEmail(),
                        user.getName(),
                        dto.getAssetSymbol(),
                        quantity,
                        dto.getAmount(),
                        userAsset.getQuantity(),
                        currentPrice,
                        user.getAccount().getBalance(),
                        netWorth
                );
            } catch (Exception e) {
                log.error("Failed to send purchase confirmation email: {}", e.getMessage());
            }

            return "Asset purchase successful.";
        } finally {
            priceCache.get().clear();
            marketService.finishTransaction();
        }
    }

    @Transactional
    public String sellAsset(String accountNumber, SellAssetDTO dto) {
        try {
            User user = validateUserAndPin(accountNumber, dto.getPin());

            if (!marketService.isValidAsset(dto.getAssetSymbol())) {
                throw new AssetNotFoundException(dto.getAssetSymbol());
            }
            Double currentPrice = marketService.getPriceForAsset(dto.getAssetSymbol());
            priceCache.get().set(dto.getAssetSymbol(), currentPrice);

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

            // Calculate net worth before sending email
            Double netWorth = calculateTotalAssetValue(user.getId()) + user.getAccount().getBalance();

            try {
                emailService.sendInvestmentSaleConfirmation(
                        user.getEmail(),
                        user.getName(),
                        dto.getAssetSymbol(),
                        dto.getQuantity(),
                        profitLoss,
                        userAsset.getQuantity(),
                        currentPrice,
                        user.getAccount().getBalance(),
                        netWorth
                );
            } catch (Exception e) {
                log.error("Failed to send sale confirmation email: {}", e.getMessage());
            }

            return "Asset sale successful.";
        } finally {
            priceCache.get().clear();
            marketService.finishTransaction();
        }
    }

    public Double calculateNetWorth(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        Double cashBalance = user.getAccount().getBalance();
        Double assetValue = calculateTotalAssetValue(user.getId());

        return cashBalance + assetValue;
    }

    private Double calculateTotalAssetValue(String userId) {
        try {
            Map<String, Double> userAssets = userAssetRepository.findUserAssetBalances(userId);
            if (userAssets == null || userAssets.isEmpty()) {
                return 0.0;
            }

            Double totalValue = 0.0;
            Map<String, Double> prices = marketService.getAllPrices();

            for (Map.Entry<String, Double> asset : userAssets.entrySet()) {
                String symbol = asset.getKey();
                Double quantity = asset.getValue();

                if (symbol != null && prices.containsKey(symbol)) {
                    Double price = prices.get(symbol);
                    if (price != null && quantity != null) {
                        totalValue += price * quantity;
                    }
                }
            }

            return totalValue;
        } catch (Exception e) {
            log.error("Error calculating total asset value: {}", e.getMessage());
            return 0.0;
        }
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

    public Map<String, Double> getUserAssets(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException(accountNumber));

        return userAssetRepository.findUserAssetBalances(user.getId());
    }
}