package com.sliit.financetracker.repository;

import com.sliit.financetracker.model.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, String> {
    List<Budget> findBudgetsByUserId(String userId);
    Optional<Budget> findBudgetByIdAndUserId(String id, String userId);
}
