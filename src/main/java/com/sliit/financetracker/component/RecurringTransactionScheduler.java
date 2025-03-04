package com.sliit.financetracker.component;

import com.sliit.financetracker.model.RecurringTransaction;
import com.sliit.financetracker.model.Transaction;
import com.sliit.financetracker.repository.RecurringTransactionRepository;
import com.sliit.financetracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

public class RecurringTransactionScheduler {

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void processRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueTransactions = recurringTransactionRepository.findByNextTransactionDate(today);

        for (RecurringTransaction recurringTransaction : dueTransactions) {

            Transaction newTransaction = new Transaction();
            newTransaction.setUserId(recurringTransaction.getUserId());
            newTransaction.setAmount(recurringTransaction.getAmount());
            newTransaction.setCategory(recurringTransaction.getCategory());
            newTransaction.setTags(recurringTransaction.getTags());
            newTransaction.setDate(today.atStartOfDay());

            transactionRepository.save(newTransaction);

            // Update next transaction date and save
            recurringTransaction.setNextTransactionDate(calculateNextDate(today, recurringTransaction.getFrequency()));
            recurringTransactionRepository.save(recurringTransaction);
        }
    }

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
