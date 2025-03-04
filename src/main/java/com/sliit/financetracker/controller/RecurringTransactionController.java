package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.RecurringTransaction;
import com.sliit.financetracker.service.RecurringTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/recurring-transaction")
public class RecurringTransactionController {
    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    // Get all recurring transactions by userId
    @GetMapping
    public ResponseEntity<?> getRecurringTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(recurringTransactionService
                .getRecurringTransactionsByUserId(userDetails.getUsername()));
    }

    // Get a single recurring transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecurringTransactionById(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(recurringTransactionService
                .getRecurringTransactionById(id, userDetails.getUsername()));
    }

    // Create a new recurring transaction
    @PostMapping
    public ResponseEntity<?> createRecurringTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody @Valid RecurringTransaction recurringTransaction) {
        recurringTransaction.setUserId(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(recurringTransactionService
                .createRecurringTransaction(recurringTransaction));
    }

    // Update an existing recurring transaction
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecurringTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable String id,
                                                        @RequestBody @Valid RecurringTransaction updatedTransaction) {
        return ResponseEntity.status(HttpStatus.OK).body(recurringTransactionService
                .updateRecurringTransaction(id, userDetails.getUsername(), updatedTransaction));
    }

    // Delete a recurring transaction by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecurringTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(recurringTransactionService
                .deleteRecurringTransaction(id, userDetails.getUsername()));
    }
}
