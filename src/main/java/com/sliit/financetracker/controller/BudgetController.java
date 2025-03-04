package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.Budget;
import com.sliit.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("api/v1/budget")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<?> getBudgets(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.getBudgetsByUserId(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.getBudgetById(id, userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<?> createBudget(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody @Valid Budget budget) {
        if ("category".equals(budget.getType())) {
            Objects.requireNonNull(budget.getCategory(), "Category should not be null");
            budget.setStartDate(null);
        } else if ("monthly".equals(budget.getType())) {
            Objects.requireNonNull(budget.getStartDate(), "Start date should not be null");
            budget.setCategory(null);
            budget.setRenewDate(budget.getStartDate().plusMonths(1));
        }

        budget.setUserId(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.createBudget(budget));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable String id,
                                          @RequestBody @Valid Budget budget) {
        if ("category".equals(budget.getType())) {
            Objects.requireNonNull(budget.getCategory(), "Category should not be null");
            budget.setStartDate(null);
        } else if ("monthly".equals(budget.getType())) {
            Objects.requireNonNull(budget.getStartDate(), "Start date should not be null");
            budget.setCategory(null);
            budget.setRenewDate(budget.getStartDate().plusMonths(1));
        }

        return ResponseEntity.status(HttpStatus.OK).body(budgetService
                .updateBudgetById(id, userDetails.getUsername(), budget));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.deleteBudgetById(id, userDetails.getUsername()));
    }
}
