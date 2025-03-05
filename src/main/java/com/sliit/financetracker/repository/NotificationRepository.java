package com.sliit.financetracker.repository;

import com.sliit.financetracker.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    boolean existsByUserIdAndBudgetIdAndMessage(String userId, String budgetId, String message);
}
