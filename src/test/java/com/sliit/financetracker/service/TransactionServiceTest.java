package com.sliit.financetracker.service;

import com.sliit.financetracker.model.Transaction;
import com.sliit.financetracker.repository.GoalRepository;
import com.sliit.financetracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId("1");
        transaction.setUserId("user123");
        transaction.setType("income");
        transaction.setAmount(100.0);
    }

    @Test
    void testCreateTransaction() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doNothing().when(notificationService).checkBudgetLimit(transaction.getUserId());

        Transaction createdTransaction = transactionService.createTransaction(transaction);

        assertNotNull(createdTransaction);
        assertEquals("1", createdTransaction.getId());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(notificationService, times(1)).checkBudgetLimit(transaction.getUserId());
    }

    @Test
    void testGetTransactionById() {
        when(transactionRepository.findByIdAndUserId("1", "user123")).thenReturn(Optional.of(transaction));

        Optional<Transaction> result = transactionService.getTransactionById("1", "user123");

        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void testGetTransactionsByUserId() {
        when(transactionRepository.findByUserId("user123")).thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.getTransactionsByUserId("user123");

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
    }

    @Test
    void testUpdateTransaction() {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(200.0);
        updatedTransaction.setType("expense");
        updatedTransaction.setCategory("Food");

        when(transactionRepository.findByIdAndUserId("1", "user123")).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        Transaction result = transactionService.updateTransaction("1", "user123", updatedTransaction);

        assertNotNull(result);
        assertEquals(200.0, result.getAmount());
    }

    @Test
    void testDeleteTransaction() {
        when(transactionRepository.findByIdAndUserId("1", "user123")).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).deleteById("1");

        Optional<Transaction> deletedTransaction = transactionService.deleteTransaction("1", "user123");

        assertTrue(deletedTransaction.isPresent());
        verify(transactionRepository, times(1)).deleteById("1");
    }

    @Test
    void geTransactionById_ShouldThrowException_WhenTransactionNotFound() {
        when(transactionRepository.findByIdAndUserId("1", "user123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.getTransactionById("1", "user123"));

        assertEquals("Transaction not found!", exception.getMessage());
    }

    @Test
    void getTransactionByUserId_ShouldThrowException_WhenNoTransactionFound() {
        when(transactionRepository.findByUserId("user123")).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                transactionService.getTransactionsByUserId("user123")
        );

        assertEquals("No transactions found!", exception.getMessage());
    }
}
