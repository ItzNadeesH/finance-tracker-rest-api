package com.sliit.financetracker.service;

import com.sliit.financetracker.model.Goal;
import com.sliit.financetracker.model.Transaction;
import com.sliit.financetracker.repository.GoalRepository;
import com.sliit.financetracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final GoalRepository goalRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public TransactionService(TransactionRepository transactionRepository,
                              NotificationService notificationService,
                              GoalRepository goalRepository) {
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.goalRepository = goalRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        Transaction newTransaction = transactionRepository.save(transaction);
        notificationService.checkBudgetLimit(transaction.getUserId());

        if (transaction.getType().equalsIgnoreCase("income")) {
            allocateToGoals(transaction.getUserId(), transaction.getAmount());
        }

        return newTransaction;
    }

    public Optional<Transaction> getTransactionById(String id, String userId) {
        Optional<Transaction> transaction = transactionRepository.findByIdAndUserId(id, userId);
        transaction.orElseThrow(() -> new RuntimeException("Transaction not found!"));

        return transaction;
    }

    public List<Transaction> getTransactionsByUserId(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        if (transactions.isEmpty()) {
            throw new  RuntimeException("No transactions found!");
        }

        return transactions;
    }

    public Transaction updateTransaction(String id, String userId, Transaction updatedTransaction) {
        Optional<Transaction> transaction = transactionRepository.findByIdAndUserId(id, userId);
        Transaction existingTransaction = transaction.orElseThrow(() -> new RuntimeException("Transaction not found!"));

        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setType(updatedTransaction.getType());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setTags(updatedTransaction.getTags());
        existingTransaction.setDescription(updatedTransaction.getDescription());

        Transaction newTransaction = transactionRepository.save(existingTransaction);
        notificationService.checkBudgetLimit(existingTransaction.getUserId());

        return newTransaction;
    }

    public Optional<Transaction> deleteTransaction(String id, String userId) {
        Optional<Transaction> transaction = transactionRepository.findByIdAndUserId(id, userId);
        transaction.orElseThrow(() -> new RuntimeException("Transaction not found!"));

        transactionRepository.deleteById(id);

        return transaction;
    }

    public List<Transaction> filterTransactions(String userId, String type, String category, List<String> tags) {
        Query query = new Query(Criteria.where("userId").is(userId));

        if (type != null) {
            query.addCriteria(Criteria.where("type").regex("^" + type + "$", "i"));
        }

        if (category != null) {
            query.addCriteria(Criteria.where("category").regex("^" + category + "$", "i"));
        }

        if (tags != null) {
            query.addCriteria(Criteria.where("tags").all(tags));
        }

        List<Transaction> transactions = mongoTemplate.find(query, Transaction.class);

        if (transactions.isEmpty()) {
            throw new RuntimeException("No transactions found!");
        }

        return transactions;
    }

    private void allocateToGoals(String userId, double incomeAmount) {
        List<Goal> goals = goalRepository.findByUserId(userId);


        for (Goal goal : goals) {
            double allocatedAmount = 0;

            if (goal.getIncomeAllocationPercentage() > 0) {
                allocatedAmount = (incomeAmount * goal.getIncomeAllocationPercentage());
            }

            if (allocatedAmount > 0) {
                goal.updateAmount(allocatedAmount);
                goalRepository.save(goal);  // Persist the updated goal progress
            }
        }
    }
}
