package com.sliit.financetracker.service;

import com.sliit.financetracker.model.FinancialReport;
import com.sliit.financetracker.model.Transaction;
import com.sliit.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinancialReportService {
    private final TransactionRepository transactionRepository;

    public FinancialReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public FinancialReport generateReport(String userId, LocalDate startDate, LocalDate endDate, String category, List<String> tags) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        // Apply category and tag filters if provided
        if (category != null) {
            transactions = transactions.stream()
                    .filter(t -> category.equals(t.getCategory()))
                    .toList();
        }

        if (tags != null && !tags.isEmpty()) {
            transactions = transactions.stream()
                    .filter(t -> t.getTags() != null && new HashSet<>(t.getTags()).containsAll(tags))
                    .collect(Collectors.toList());
        }

        // Calculate total income and expenses
        double totalIncome = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Group transactions by category and calculate total spending per category
        Map<String, Double> expensesByCategory = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> incomesByCategory = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income"))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));


        return new FinancialReport(startDate, endDate, totalIncome, totalExpenses, expensesByCategory, incomesByCategory);
    }
}
