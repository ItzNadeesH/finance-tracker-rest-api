package com.sliit.financetracker.service;

import com.sliit.financetracker.model.*;
import com.sliit.financetracker.repository.BudgetRepository;
import com.sliit.financetracker.repository.NotificationRepository;
import com.sliit.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               TransactionRepository transactionRepository,
                               BudgetRepository budgetRepository) {
        this.notificationRepository = notificationRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;

    }

    public List<Notification> getNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        if (notifications.isEmpty()) {
            throw new  RuntimeException("No notifications found!");
        }

        return notifications;
    }

    public Notification getNotificationById(String id, String userId) {
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found!"));

        notification.setRead(true);
        notificationRepository.save(notification);

        return notification;
    }

    public void checkBudgetLimit(String userId) {
        List<Budget> budgets = budgetRepository.findBudgetsByUserId(userId);

        budgets.forEach((budget) -> {

            if (budget.getType().equals("category")) {
                List<Transaction> transactions = transactionRepository
                        .findByUserIdAndDateBetween(userId, budget.getStartDate(), LocalDate.now().plusDays(1));

                double totalExpense = transactions.stream()
                        .filter(t -> t.getType().equalsIgnoreCase("expense") &&
                                t.getCategory().equalsIgnoreCase(budget.getCategory()))
                        .mapToDouble(Transaction::getAmount)
                        .sum();

                if (totalExpense > budget.getAmount()) {
                    sendNotification(userId, budget.getId(), "You have exceeded your budget for " + budget.getCategory());
                } else if (totalExpense > budget.getLimit()) {
                    sendNotification(userId, budget.getId(), "You have exceeded 90% your budget for " + budget.getCategory());
                }

            } else if (budget.getType().equals("monthly")) {
                List<Transaction> transactions = transactionRepository
                        .findByUserIdAndDateBetween(userId, budget.getStartDate(), budget.getRenewDate());

                double totalExpense = transactions.stream()
                        .filter(t -> t.getType().equalsIgnoreCase("expense"))
                        .mapToDouble(Transaction::getAmount)
                        .sum();

                if (totalExpense > budget.getAmount()) {
                    sendNotification(userId, budget.getId(), "You have exceeded your monthly budget!");
                } else if (totalExpense > budget.getLimit()) {
                    sendNotification(userId, budget.getId(), "You have exceeded 90% your monthly budget!");
                }
            }
        });
    }

    public void sendNotification(String userId, String budgetId, String message) {
        if (notificationRepository.existsByUserIdAndBudgetIdAndMessage(userId, budgetId, message)) {
            return;
        }
        Notification notification = new Notification(userId, budgetId, message,LocalDateTime.now());

        // Save the notification to DB, send email, or push notification
        notificationRepository.save(notification);
    }


}
