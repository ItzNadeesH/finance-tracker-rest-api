package com.sliit.financetracker.service;

import com.sliit.financetracker.model.*;
import com.sliit.financetracker.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public CurrencyService(UserRepository userRepository,
                       GoalRepository goalRepository,
                       BudgetRepository budgetRepository,
                       TransactionRepository transactionRepository,
                       RecurringTransactionRepository recurringTransactionRepository) {
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }
    @Value("${exchange.rate.api.key}")
    private String apiKey;

    private double getExchangeRate(String baseCurrency, String targetCurrency) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + baseCurrency;

        Map response = restTemplate.getForObject(url, Map.class);
        if (response != null && "success".equals(response.get("result"))) {
            Map<String, Double> rates = (Map<String, Double>) response.get("conversion_rates");
            return rates.getOrDefault(targetCurrency, 1.0); // Default to 1.0 if currency not found
        }

        return 1.0;
    }

    private double convertAmount(double amount, String from, String to) {
        double rate = getExchangeRate(from, to);
        return amount * rate;
    }

    public User changeCurrency(String userId, String currency) {
        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (user.getCurrency().equals(currency)) {
            return user;
        }

        List<Goal> goals = goalRepository.findByUserId(userId);
        List<Budget> budgets = budgetRepository.findBudgetsByUserId(userId);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findByUserId(userId);

        goals.forEach(goal -> {
            goal.setCurrentAmount(convertAmount(goal.getCurrentAmount(), user.getCurrency(), currency));
            goal.setTargetAmount(convertAmount(goal.getTargetAmount(), user.getCurrency(), currency));
        });

        budgets.forEach(budget -> {
            budget.setAmount(convertAmount(budget.getAmount(), user.getCurrency(), currency));
            budget.setLimit(convertAmount(budget.getLimit(), user.getCurrency(), currency));
        });

        transactions.forEach(transaction -> {
            transaction.setAmount(convertAmount(transaction.getAmount(), user.getCurrency(), currency));
        });

        recurringTransactions.forEach(transaction -> {
            transaction.setAmount(convertAmount(transaction.getAmount(), user.getCurrency(), currency));
        });

        goalRepository.saveAll(goals);
        budgetRepository.saveAll(budgets);
        transactionRepository.saveAll(transactions);
        recurringTransactionRepository.saveAll(recurringTransactions);

        user.setCurrency(currency);
        userRepository.save(user);

        return user;
    }
}
