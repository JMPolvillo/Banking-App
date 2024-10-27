package com.hackathon.bankingapp.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP");
        message.setText("OTP:" + otp);

        javaMailSender.send(message);
    }

    public void sendPasswordResetConfirmation(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Successful");
        message.setText("Your password has been successfully reset.");

        javaMailSender.send(message);
    }

    public void sendInvestmentPurchaseConfirmation(
            String to,
            String userName,
            String symbol,
            Double quantity,
            Double amount,
            Double totalHoldings,
            Double currentPrice,
            Double accountBalance,
            Double netWorth) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Investment Purchase Confirmation");
        message.setText(String.format("""
            Dear %s,

            You have successfully purchased %.2f units of %s for a total amount of $%.2f.

            Current holdings of %s: %.2f units

            Summary of current assets:
            - %s: %.2f units purchased at $%.2f

            Account Balance: $%.2f
            Net Worth: $%.2f

            Thank you for using our investment services.

            Best Regards,
            Investment Management Team
            """,
                userName,
                quantity,
                symbol,
                amount,
                symbol,
                totalHoldings,
                symbol,
                totalHoldings,
                currentPrice,
                accountBalance,
                netWorth));

        javaMailSender.send(message);
    }

    public void sendInvestmentSaleConfirmation(
            String to,
            String userName,
            String symbol,
            Double quantity,
            Double profitLoss,
            Double remainingHoldings,
            Double currentPrice,
            Double accountBalance,
            Double netWorth) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Investment Sale Confirmation");
        message.setText(String.format("""
            Dear %s,

            You have successfully sold %.2f units of %s.

            Total Gain/Loss: $%.2f

            Remaining holdings of %s: %.2f units

            Summary of current assets:
            - %s: %.2f units purchased at $%.2f

            Account Balance: $%.2f
            Net Worth: $%.2f

            Thank you for using our investment services.

            Best Regards,
            Investment Management Team
            """,
                userName,
                quantity,
                symbol,
                profitLoss,
                symbol,
                remainingHoldings,
                symbol,
                remainingHoldings,
                currentPrice,
                accountBalance,
                netWorth));

        javaMailSender.send(message);
    }
}