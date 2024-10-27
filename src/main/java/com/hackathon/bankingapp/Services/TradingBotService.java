package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.AutoInvestDTO;
import com.hackathon.bankingapp.DTO.BuyAssetDTO;
import com.hackathon.bankingapp.DTO.SellAssetDTO;
import com.hackathon.bankingapp.Entities.*;
import com.hackathon.bankingapp.Exceptions.*;
import com.hackathon.bankingapp.Repositories.ITradingBotRepository;
import com.hackathon.bankingapp.Repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingBotService {

    private final ITradingBotRepository tradingBotRepository;
    private final IUserRepository userRepository;
    private final MarketService marketService;
    private final AssetService assetService;
    private final PasswordEncoder passwordEncoder;

    private static final int CHECK_INTERVAL_SECONDS = 30;

    @Transactional
    public String enableAutoInvest(String accountNumber, AutoInvestDTO dto) {

        User user = validateUserAndPin(accountNumber, dto.getPin());

        TradingBot bot = tradingBotRepository.findByUserId(user.getId())
                .orElse(new TradingBot());

        bot.setUser(user);
        bot.setActive(true);
        bot.setLastCheckTime(System.currentTimeMillis());
        bot.setLastPrices(marketService.getAllPrices());

        tradingBotRepository.save(bot);
        log.info("Auto-investment enabled for user: {}", accountNumber);

        return "Automatic investment enabled successfully.";
    }

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void processAutomaticTrading() {
        List<TradingBot> activeBots = tradingBotRepository.findBotsWithSufficientBalance();
        Map<String, Double> currentPrices = marketService.getAllPrices();

        for (TradingBot bot : activeBots) {
            try {
                processBot(bot, currentPrices);
            } catch (Exception e) {
                log.error("Error processing bot {}: {}", bot.getId(), e.getMessage());
                bot.setActive(false);
                tradingBotRepository.save(bot);
            }
        }
    }

    private void processBot(TradingBot bot, Map<String, Double> currentPrices) {
        Map<String, Double> lastPrices = bot.getLastPrices();

        currentPrices.forEach((symbol, currentPrice) -> {
            Double lastPrice = lastPrices.get(symbol);
            if (lastPrice != null) {
                double priceChange = ((currentPrice - lastPrice) / lastPrice) * 100;

                if (priceChange <= -bot.getBuyThreshold()) {
                    tryToBuy(bot, symbol, currentPrice);
                } else if (priceChange >= bot.getSellThreshold()) {
                    tryToSell(bot, symbol, currentPrice);
                }
            }
        });

        bot.setLastPrices(currentPrices);
        bot.setLastCheckTime(System.currentTimeMillis());
        tradingBotRepository.save(bot);
    }

    private void tryToBuy(TradingBot bot, String symbol, Double currentPrice) {
        try {
            if (bot.getUser().getAccount().getBalance() >= bot.getInvestmentAmount()) {
                assetService.buyAsset(bot.getUser().getAccountNumber(),
                        createBuyAssetDTO(bot, symbol));
                log.info("Bot bought {} for user {}", symbol, bot.getUser().getAccountNumber());
            }
        } catch (Exception e) {
            log.error("Error buying asset {}: {}", symbol, e.getMessage());
        }
    }

    private void tryToSell(TradingBot bot, String symbol, Double currentPrice) {
        try {

            Map<String, Double> userAssets = assetService.getUserAssets(
                    bot.getUser().getAccountNumber());

            Double quantity = userAssets.get(symbol);
            if (quantity != null && quantity > 0) {
                assetService.sellAsset(bot.getUser().getAccountNumber(),
                        createSellAssetDTO(bot, symbol, quantity));
                log.info("Bot sold {} for user {}", symbol, bot.getUser().getAccountNumber());
            }
        } catch (Exception e) {
            log.error("Error selling asset {}: {}", symbol, e.getMessage());
        }
    }

    private BuyAssetDTO createBuyAssetDTO(TradingBot bot, String symbol) {
        BuyAssetDTO dto = new BuyAssetDTO();
        dto.setAssetSymbol(symbol);
        dto.setAmount(bot.getInvestmentAmount());
        dto.setPin(bot.getUser().getPin());
        return dto;
    }

    private SellAssetDTO createSellAssetDTO(TradingBot bot, String symbol, Double quantity) {
        SellAssetDTO dto = new SellAssetDTO();
        dto.setAssetSymbol(symbol);
        dto.setQuantity(quantity);
        dto.setPin(bot.getUser().getPin());
        return dto;
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