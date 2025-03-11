package com.sliit.financetracker.service;

import com.sliit.financetracker.model.*;
import com.sliit.financetracker.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final NotificationRepository notificationRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public NotificationService(RecurringTransactionRepository recurringTransactionRepository,
                               NotificationRepository notificationRepository,
                               TransactionRepository transactionRepository,
                               BudgetRepository budgetRepository,
                               UserRepository userRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.notificationRepository = notificationRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
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

    @Scheduled(fixedRate = 86400000) // Run every 24 hours
    public void sendDailyNotifications() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            checkBudgetLimit(user.getUsername());
            checkRecurringTransactions(user.getUsername());
        }
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

    public void checkRecurringTransactions(String userId) {
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findByUserId(userId);
        LocalDate today = LocalDate.now();

        recurringTransactions.forEach(transaction -> {
            if (transaction.getNextTransactionDate().isEqual(today.plusDays(3))) {
                sendNotification(userId, transaction.getId(),"Reminder: Your " + transaction.getPaymentName() +
                        " is due in 3 days.");
            } else if (transaction.getNextTransactionDate().isBefore(today)) {
                sendNotification(userId, transaction.getId(),"Missed Transaction Alert: You missed a payment for " +
                        transaction.getPaymentName() + " on " + transaction.getNextTransactionDate());
            }
        });
    }

    public void sendNotification(String userId, String referenceId, String message) {
        if (notificationRepository.existsByUserIdAndReferenceIdAndMessage(userId, referenceId, message)) {
            return;
        }
        Notification notification = new Notification(userId, referenceId, message,LocalDateTime.now());

        // Save the notification to DB, send email, or push notification
        notificationRepository.save(notification);
    }


}
