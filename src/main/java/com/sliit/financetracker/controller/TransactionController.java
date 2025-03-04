package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.Transaction;
import com.sliit.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService
                .getTransactionsByUserId(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<?> addTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody @Valid Transaction transaction) {
        transaction.setUserId(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(transactionService
                .createTransaction(transaction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService
                .getTransactionById(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable String id,
                                               @RequestBody @Valid Transaction transaction) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService
                .updateTransaction(id, userDetails.getUsername(), transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService
                .deleteTransaction(id, userDetails.getUsername()));
    }

}
