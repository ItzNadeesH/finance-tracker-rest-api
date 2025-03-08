package com.sliit.financetracker.service;

import com.sliit.financetracker.model.RecurringTransaction;
import com.sliit.financetracker.repository.RecurringTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionServiceTest {

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @InjectMocks
    private RecurringTransactionService recurringTransactionService;

    private RecurringTransaction recurringTransaction;
    private final String userId = "user123";
    private final String transactionId = "txn123";

    @BeforeEach
    void setUp() {
        recurringTransaction = new RecurringTransaction();
        recurringTransaction.setUserId(userId);
        recurringTransaction.setPaymentName("Rent");
        recurringTransaction.setType("Expense");
        recurringTransaction.setAmount(1000.0);
        recurringTransaction.setFrequency("Monthly");
        recurringTransaction.setPaymentsCount("12");
        recurringTransaction.setCategory("Housing");
        recurringTransaction.setDescription("Monthly rent payment");
        recurringTransaction.setStartDate(LocalDate.of(2024, 3, 1));
    }

    @Test
    void testCreateRecurringTransaction() {
        when(recurringTransactionRepository.save(any(RecurringTransaction.class))).thenReturn(recurringTransaction);

        RecurringTransaction savedTransaction = recurringTransactionService.createRecurringTransaction(recurringTransaction);

        assertNotNull(savedTransaction);
        assertEquals("Rent", savedTransaction.getPaymentName());
        assertEquals(1000, savedTransaction.getAmount());
        assertEquals(LocalDate.of(2024, 4, 1), savedTransaction.getNextTransactionDate()); // Monthly frequency
    }

    @Test
    void testGetRecurringTransactionById() {
        when(recurringTransactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(recurringTransaction));

        Optional<RecurringTransaction> foundTransaction = recurringTransactionService.getRecurringTransactionById(transactionId, userId);

        assertTrue(foundTransaction.isPresent());
        assertEquals("Rent", foundTransaction.get().getPaymentName());
    }

    @Test
    void testGetRecurringTransactionByIdThrowsExceptionIfNotFound() {
        when(recurringTransactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recurringTransactionService.getRecurringTransactionById(transactionId, userId);
        });

        assertEquals("Recurring transaction not found!", exception.getMessage());
    }

    @Test
    void testGetRecurringTransactionsByUserId() {
        when(recurringTransactionRepository.findByUserId(userId)).thenReturn(List.of(recurringTransaction));

        List<RecurringTransaction> transactions = recurringTransactionService.getRecurringTransactionsByUserId(userId);

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals("Rent", transactions.get(0).getPaymentName());
    }

    @Test
    void testGetRecurringTransactionsByUserIdThrowsExceptionIfEmpty() {
        when(recurringTransactionRepository.findByUserId(userId)).thenReturn(List.of());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recurringTransactionService.getRecurringTransactionsByUserId(userId);
        });

        assertEquals("No recurring transactions found!", exception.getMessage());
    }

    @Test
    void testUpdateRecurringTransactionThrowsExceptionIfNotFound() {
        RecurringTransaction updatedTransaction = new RecurringTransaction();
        updatedTransaction.setPaymentName("Updated Rent");

        when(recurringTransactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recurringTransactionService.updateRecurringTransaction(transactionId, userId, updatedTransaction);
        });

        assertEquals("Recurring transaction not found!", exception.getMessage());
    }

    @Test
    void testDeleteRecurringTransaction() {
        when(recurringTransactionRepository.findByIdAndUserId(transactionId, userId))
                .thenReturn(Optional.of(recurringTransaction));

        Optional<RecurringTransaction> deletedTransaction = recurringTransactionService.deleteRecurringTransaction(transactionId, userId);

        verify(recurringTransactionRepository, times(1)).deleteById(transactionId);
        assertTrue(deletedTransaction.isPresent());
        assertEquals("Rent", deletedTransaction.get().getPaymentName());
    }

    @Test
    void testDeleteRecurringTransactionThrowsExceptionIfNotFound() {
        when(recurringTransactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recurringTransactionService.deleteRecurringTransaction(transactionId, userId);
        });

        assertEquals("Recurring transaction not found!", exception.getMessage());
    }
}
