package com.sliit.financetracker.repository;

import com.sliit.financetracker.model.RecurringTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository extends MongoRepository<RecurringTransaction, String> {
    List<RecurringTransaction> findByUserId(String userId);
    Optional<RecurringTransaction> findByIdAndUserId(String id, String userId);
    List<RecurringTransaction> findByNextTransactionDate(LocalDate date);
}
