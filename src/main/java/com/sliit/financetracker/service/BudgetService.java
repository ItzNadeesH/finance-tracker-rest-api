package com.sliit.financetracker.service;

import com.sliit.financetracker.model.Budget;
import com.sliit.financetracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget createBudget(Budget budget) {
        budget.setLimit(Math.round(budget.getAmount() * 0.9));
        return budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsByUserId(String userId) {
        List<Budget> budgets = budgetRepository.findBudgetsByUserId(userId);

        if (budgets.isEmpty()) {
            throw new RuntimeException("No budgets found!");
        }
        return budgets;
    }

    public Optional<Budget> getBudgetById(String id, String userId) {
        Optional<Budget> budget = budgetRepository.findBudgetByIdAndUserId(id, userId);
        budget.orElseThrow(() -> new RuntimeException("Budget not found!"));

        return budget;
    }

    public Budget updateBudgetById(String id, String userId, Budget updatedBudget) {
        Optional<Budget> budget = budgetRepository.findBudgetByIdAndUserId(id ,userId);
        Budget existingbudget = budget.orElseThrow(() -> new RuntimeException("Budget not found!"));

        existingbudget.setAmount(updatedBudget.getAmount());
        existingbudget.setType(updatedBudget.getType());
        existingbudget.setCategory(updatedBudget.getCategory());
        existingbudget.setStartDate(updatedBudget.getStartDate());
        existingbudget.setRenewDate(updatedBudget.getRenewDate());
        existingbudget.setLimit(Math.round(updatedBudget.getAmount() * 0.9));

        return budgetRepository.save(existingbudget);
    }

    public Optional<Budget> deleteBudgetById(String id, String userId) {
        Optional<Budget> budget = budgetRepository.findBudgetByIdAndUserId(id, userId);
        budget.orElseThrow(() -> new RuntimeException("Budget not found!"));

        budgetRepository.deleteById(id);
        return budget;
    }

}
