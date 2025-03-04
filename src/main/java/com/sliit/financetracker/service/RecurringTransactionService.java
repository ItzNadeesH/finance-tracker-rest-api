package com.sliit.financetracker.service;

import com.sliit.financetracker.model.RecurringTransaction;
import com.sliit.financetracker.repository.RecurringTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringTransactionService {
    private final RecurringTransactionRepository recurringTransactionRepository;

    public RecurringTransactionService(RecurringTransactionRepository recurringTransactionRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    // Create a new recurring transaction and set the next transaction date
    public RecurringTransaction createRecurringTransaction(RecurringTransaction transaction) {
        transaction.setNextTransactionDate(calculateNextDate(transaction.getStartDate(), transaction.getFrequency()));
        return recurringTransactionRepository.save(transaction);
    }

    // Get a recurring transaction by its ID and userId
    public Optional<RecurringTransaction> getRecurringTransactionById(String id, String userId) {
        Optional<RecurringTransaction> transaction = recurringTransactionRepository.findByIdAndUserId(id, userId);
        transaction.orElseThrow(() -> new RuntimeException("Recurring transaction not found!"));
        return transaction;
    }

    // Get all recurring transactions by userId
    public List<RecurringTransaction> getRecurringTransactionsByUserId(String userId) {
        List<RecurringTransaction> transactions = recurringTransactionRepository.findByUserId(userId);
        if (transactions.isEmpty()) {
            throw new RuntimeException("No recurring transactions found!");
        }
        return transactions;
    }

    // Update a recurring transaction
    public RecurringTransaction updateRecurringTransaction(String id, String userId, RecurringTransaction updatedTransaction) {
        Optional<RecurringTransaction> transaction = recurringTransactionRepository.findByIdAndUserId(id, userId);
        RecurringTransaction existingTransaction = transaction.orElseThrow(() -> new RuntimeException("Recurring transaction not found!"));

        existingTransaction.setPaymentName(updatedTransaction.getPaymentName());
        existingTransaction.setType(updatedTransaction.getType());
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setFrequency(updatedTransaction.getFrequency());
        existingTransaction.setPaymentsCount(updatedTransaction.getPaymentsCount());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setTags(updatedTransaction.getTags());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setStartDate(updatedTransaction.getStartDate());

        existingTransaction.setNextTransactionDate(calculateNextDate(existingTransaction.getStartDate(), existingTransaction.getFrequency()));

        return recurringTransactionRepository.save(existingTransaction);
    }

    // Delete a recurring transaction by ID and userId
    public Optional<RecurringTransaction> deleteRecurringTransaction(String id, String userId) {
        Optional<RecurringTransaction> transaction = recurringTransactionRepository.findByIdAndUserId(id, userId);
        transaction.orElseThrow(() -> new RuntimeException("Recurring transaction not found!"));

        recurringTransactionRepository.deleteById(id);

        return transaction;
    }

    // Calculate the next transaction date based on frequency (Daily, Weekly, Monthly, Yearly)
    private LocalDate calculateNextDate(LocalDate currentDate, String frequency) {
        return switch (frequency) {
            case "Daily" -> currentDate.plusDays(1);
            case "Weekly" -> currentDate.plusWeeks(1);
            case "Monthly" -> currentDate.plusMonths(1);
            case "Yearly" -> currentDate.plusYears(1);
            default -> currentDate;
        };
    }
}
