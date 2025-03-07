package com.sliit.financetracker.service;

import com.sliit.financetracker.model.*;
import com.sliit.financetracker.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService currencyService;

    private User user;
    private Goal goal;
    private Budget budget;
    private Transaction transaction;
    private RecurringTransaction recurringTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("user1", "user@mail.com", "123");
        user.setCurrency("USD");

        goal = new Goal("user1", 1000, LocalDate.now());
        goal.setCurrentAmount(500);


        budget = new Budget();
        budget.setUserId("user1");
        budget.setAmount(200);
        budget.setLimit(180);

        transaction = new Transaction();
        transaction.setUserId("user1");
        transaction.setAmount(50.0);

        recurringTransaction = new RecurringTransaction();
        recurringTransaction.setUserId("user1");
        recurringTransaction.setAmount(30.0);
    }

    @Test
    void changeCurrency_ShouldNotUpdateWhenCurrencyIsSame() {
        // Same currency, so no update should happen
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        User updatedUser = currencyService.changeCurrency("user1", "USD");

        // The user currency should not change
        assertEquals("USD", updatedUser.getCurrency());

        // Verify that no updates were made to other entities
        verify(goalRepository, never()).saveAll(anyList());
        verify(budgetRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).saveAll(anyList());
        verify(recurringTransactionRepository, never()).saveAll(anyList());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeCurrency_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> currencyService.changeCurrency("user1", "EUR"));

        assertEquals("User not found!", exception.getMessage());
    }
}
