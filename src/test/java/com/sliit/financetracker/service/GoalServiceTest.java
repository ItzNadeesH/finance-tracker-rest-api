package com.sliit.financetracker.service;

import com.sliit.financetracker.model.Budget;
import com.sliit.financetracker.model.Goal;
import com.sliit.financetracker.repository.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito in JUnit 5
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;

    @BeforeEach
    void setUp() {
        goal = new Goal("Saving for a car", 200000.0, LocalDate.now().plusMonths(1));
        goal.setId("goal123");
        goal.setUserId("user123");
        goal.setCurrentAmount(50000.0);
    }

    @Test
    void createGoal_shouldReturnCreatedGoal() {
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        Goal createdGoal = goalService.createGoal(goal);

        assertNotNull(createdGoal);
        assertEquals("Saving for a car", createdGoal.getName());
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void getGoalsByUserId_shouldReturnGoals() {
        when(goalRepository.findByUserId("user123")).thenReturn(List.of(goal));

        List<Goal> goals = goalService.getGoalsByUserId("user123");

        assertFalse(goals.isEmpty());
        assertEquals(1, goals.size());
        assertEquals("Saving for a car", goals.get(0).getName());
        verify(goalRepository, times(1)).findByUserId("user123");
    }

    @Test
    void getGoalsByUserId_shouldThrowExceptionWhenNoGoals() {
        when(goalRepository.findByUserId("user123")).thenReturn(List.of());

        Exception exception = assertThrows(RuntimeException.class, () -> goalService.getGoalsByUserId("user123"));

        assertEquals("No goals found!", exception.getMessage());
    }

    @Test
    void getGoalById_shouldReturnGoal() {
        when(goalRepository.findByIdAndUserId("goal123", "user123")).thenReturn(Optional.of(goal));

        Goal foundGoal = goalService.getGoalById("goal123", "user123");

        assertNotNull(foundGoal);
        assertEquals("Saving for a car", foundGoal.getName());
        verify(goalRepository, times(1)).findByIdAndUserId("goal123", "user123");
    }

    @Test
    void getGoalById_shouldThrowExceptionWhenGoalNotFound() {
        when(goalRepository.findByIdAndUserId("goal123", "user123")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> goalService.getGoalById("goal123", "user123"));

        assertEquals("Goal not found!", exception.getMessage());
    }

    @Test
    void updateGoalById_shouldUpdateAndReturnGoal() {
        Goal updatedGoal = new Goal("New Goal", 300000.0, LocalDate.now().plusMonths(2));
        updatedGoal.setCurrentAmount(75000.0);

        when(goalRepository.findByIdAndUserId("goal123", "user123")).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(updatedGoal);

        Goal result = goalService.updateGoalById("goal123", "user123", updatedGoal);

        assertNotNull(result);
        assertEquals("New Goal", result.getName());
        assertEquals(300000.0, result.getTargetAmount());
        assertEquals(75000.0, result.getCurrentAmount());
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void deleteGoalById_shouldDeleteGoal() {
        when(goalRepository.findByIdAndUserId("goal123", "user123")).thenReturn(Optional.of(goal));

        Optional<Goal> deletedGoal = Optional.ofNullable(goalService.deleteGoalById("goal123", "user123"));

        assertTrue(deletedGoal.isPresent());
        verify(goalRepository, times(1)).findByIdAndUserId("goal123", "user123");
        verify(goalRepository, times(1)).deleteById("goal123");
    }

    @Test
    void deleteGoalById_shouldThrowExceptionWhenGoalNotFound() {
        when(goalRepository.findByIdAndUserId("goal123", "user123")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> goalService.deleteGoalById("goal123", "user123"));

        assertEquals("Goal not found!", exception.getMessage());
    }

    @Test
    void updateProgressById_shouldUpdateGoalProgress() {
        when(goalRepository.findByIdAndUserId("goal123", "user123")).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        Goal updatedGoal = goalService.updateProgressById("goal123", "user123", 20000.0);

        assertNotNull(updatedGoal);
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void updateProgressById_shouldThrowExceptionWhenGoalAlreadyCompleted() {
        goal.setProgress("100%");
        when(goalRepository.findByIdAndUserId("goal123", "user123")).thenReturn(Optional.of(goal));

        Exception exception = assertThrows(RuntimeException.class, () -> goalService.updateProgressById("goal123", "user123", 10000.0));

        assertEquals("Goal is already completed", exception.getMessage());
    }

    @Test
    void checkGoalProgressAndSendNotifications_shouldSendNotifications() {
        goal.setTargetDate(LocalDate.now().plusDays(7));
        when(goalRepository.findAll()).thenReturn(List.of(goal));

        goalService.checkGoalProgressAndSendNotifications();

        verify(notificationService, times(1)).sendNotification(
                eq(goal.getUserId()), eq(goal.getId()), anyString()
        );
    }
}
