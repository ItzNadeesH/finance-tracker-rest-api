package com.sliit.financetracker.service;

import com.sliit.financetracker.model.Transaction;
import com.sliit.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
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

        return transactionRepository.save(existingTransaction);
    }

    public Optional<Transaction> deleteTransaction(String id, String userId) {
        Optional<Transaction> transaction = transactionRepository.findByIdAndUserId(id, userId);
        transaction.orElseThrow(() -> new RuntimeException("Transaction not found!"));

        transactionRepository.deleteById(id);

        return transaction;
    }
}
