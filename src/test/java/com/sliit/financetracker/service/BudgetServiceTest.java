package com.sliit.financetracker.service;

import com.sliit.financetracker.model.Budget;
import com.sliit.financetracker.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Budget budget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        budget = new Budget();
        budget.setId("1");
        budget.setUserId("user1");
        budget.setAmount(100.0);
        budget.setType("Personal");
        budget.setCategory("Food");
        budget.setStartDate(LocalDate.parse("2025-01-01"));
        budget.setRenewDate(LocalDate.parse("2025-12-31"));
    }

    @Test
    void createBudget_ShouldReturnCreatedBudget() {
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget createdBudget = budgetService.createBudget(budget);

        assertNotNull(createdBudget);
        assertEquals(90.0, createdBudget.getLimit());  // Ensure the limit is calculated correctly (90% of the amount)
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void getBudgetsByUserId_ShouldReturnListOfBudgets() {
        when(budgetRepository.findBudgetsByUserId("user1")).thenReturn(List.of(budget));

        List<Budget> budgets = budgetService.getBudgetsByUserId("user1");

        assertNotNull(budgets);
        assertFalse(budgets.isEmpty());
        verify(budgetRepository, times(1)).findBudgetsByUserId("user1");
    }

    @Test
    void getBudgetsByUserId_ShouldThrowException_WhenNoBudgetsFound() {
        when(budgetRepository.findBudgetsByUserId("user1")).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> budgetService.getBudgetsByUserId("user1"));

        assertEquals("No budgets found!", exception.getMessage());
    }

    @Test
    void getBudgetById_ShouldReturnBudget() {
        when(budgetRepository.findBudgetByIdAndUserId("1", "user1")).thenReturn(Optional.of(budget));

        Optional<Budget> foundBudget = budgetService.getBudgetById("1", "user1");

        assertTrue(foundBudget.isPresent());
        assertEquals("1", foundBudget.get().getId());
        verify(budgetRepository, times(1)).findBudgetByIdAndUserId("1", "user1");
    }

    @Test
    void getBudgetById_ShouldThrowException_WhenBudgetNotFound() {
        when(budgetRepository.findBudgetByIdAndUserId("1", "user1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> budgetService.getBudgetById("1", "user1"));

        assertEquals("Budget not found!", exception.getMessage());
    }

    @Test
    void updateBudgetById_ShouldReturnUpdatedBudget() {
        when(budgetRepository.findBudgetByIdAndUserId("1", "user1")).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget updatedBudget = new Budget();
        updatedBudget.setAmount(150.0);
        updatedBudget.setType("Personal");
        updatedBudget.setCategory("Food");
        updatedBudget.setStartDate(LocalDate.parse("2025-01-01"));
        updatedBudget.setRenewDate(LocalDate.parse("2025-12-31"));

        Budget returnedBudget = budgetService.updateBudgetById("1", "user1", updatedBudget);

        assertNotNull(returnedBudget);
        assertEquals(135.0, returnedBudget.getLimit());  // Ensure the limit is updated correctly (90% of the amount)
        verify(budgetRepository, times(1)).findBudgetByIdAndUserId("1", "user1");
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void deleteBudgetById_ShouldReturnDeletedBudget() {
        when(budgetRepository.findBudgetByIdAndUserId("1", "user1")).thenReturn(Optional.of(budget));

        Optional<Budget> deletedBudget = budgetService.deleteBudgetById("1", "user1");

        assertTrue(deletedBudget.isPresent());
        verify(budgetRepository, times(1)).findBudgetByIdAndUserId("1", "user1");
        verify(budgetRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteBudgetById_ShouldThrowException_WhenBudgetNotFound() {
        when(budgetRepository.findBudgetByIdAndUserId("1", "user1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> budgetService.deleteBudgetById("1", "user1"));

        assertEquals("Budget not found!", exception.getMessage());
    }
}

