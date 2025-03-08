package com.sliit.financetracker.service;

import com.sliit.financetracker.model.*;
import com.sliit.financetracker.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private String userId = "user123";
    private String notificationId = "notif123";

    @BeforeEach
    void setUp() {
        notification = new Notification(userId, "budget1", "Budget limit exceeded!", LocalDateTime.now());
    }

    @Test
    void testGetNotifications() {
        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(notification));

        List<Notification> notifications = notificationService.getNotifications(userId);

        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());
        assertEquals("Budget limit exceeded!", notifications.get(0).getMessage());
    }

    @Test
    void testGetNotificationsThrowsExceptionIfEmpty() {
        when(notificationRepository.findByUserId(userId)).thenReturn(List.of());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            notificationService.getNotifications(userId);
        });

        assertEquals("No notifications found!", exception.getMessage());
    }

    @Test
    void testGetNotificationById() {
        when(notificationRepository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.of(notification));

        Notification foundNotification = notificationService.getNotificationById(notificationId, userId);

        assertTrue(foundNotification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testGetNotificationByIdThrowsExceptionIfNotFound() {
        when(notificationRepository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            notificationService.getNotificationById(notificationId, userId);
        });

        assertEquals("Notification not found!", exception.getMessage());
    }

    @Test
    void testCheckBudgetLimit() {
        Budget budget = new Budget();
        budget.setType("category");
        budget.setCategory("Food");
        budget.setAmount(500);
        budget.setLimit(450);
        budget.setStartDate(LocalDate.now());
        budget.setRenewDate(LocalDate.now().plusDays(30));

        when(budgetRepository.findBudgetsByUserId(userId)).thenReturn(List.of(budget));

        Transaction expense = new Transaction();
        expense.setUserId(userId);
        expense.setType("expense");
        expense.setAmount(600.0);
        expense.setCategory("Food");
        expense.setDate(LocalDateTime.now());

        when(transactionRepository.findByUserIdAndDateBetween(eq(userId), any(), any()))
                .thenReturn(List.of(expense));

        notificationService.checkBudgetLimit(userId);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCheckRecurringTransactions() {
        RecurringTransaction recurringTransaction = new RecurringTransaction();
        recurringTransaction.setUserId(userId);
        recurringTransaction.setPaymentName("Rent");
        recurringTransaction.setNextTransactionDate(LocalDate.now().plusDays(3));

        when(recurringTransactionRepository.findByUserId(userId)).thenReturn(List.of(recurringTransaction));

        notificationService.checkRecurringTransactions(userId);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testSendNotification() {
        when(notificationRepository.existsByUserIdAndReferenceIdAndMessage(any(), any(), any())).thenReturn(false);

        notificationService.sendNotification(userId, "ref123", "Test Message");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testSendNotificationDoesNotSaveIfExists() {
        when(notificationRepository.existsByUserIdAndReferenceIdAndMessage(any(), any(), any())).thenReturn(true);

        notificationService.sendNotification(userId, "ref123", "Test Message");

        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
