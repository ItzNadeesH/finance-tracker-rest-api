package com.sliit.financetracker.repository;

import com.sliit.financetracker.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    boolean existsByUserIdAndBudgetIdAndMessage(String userId, String budgetId, String message);
    List<Notification> findByUserId(String userId);
    Optional<Notification> findByIdAndUserId(String id, String userId);
}
