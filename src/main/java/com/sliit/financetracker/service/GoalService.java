package com.sliit.financetracker.service;

import com.sliit.financetracker.model.Goal;
import com.sliit.financetracker.repository.GoalRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final NotificationService notificationService;

    public GoalService(GoalRepository goalRepository, NotificationService notificationService) {
        this.goalRepository = goalRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Runs every day at 9 AM
    public void checkGoalProgressAndSendNotifications() {
        List<Goal> goals = goalRepository.findAll();

        for (Goal goal : goals) {

            LocalDate today = LocalDate.now();
            LocalDate deadline = goal.getTargetDate();

                // Send notification when goal is nearing deadline
                if (deadline.minusDays(7).isEqual(today)) {
                    notificationService.sendNotification(goal.getUserId(), goal.getId(),
                            "Your goal to save for " + goal.getName() + " is due in 7 days!");
                }

                // Send notification when goal is achieved
                if (goal.getProgress().equals("100%")) {
                    notificationService.sendNotification(goal.getUserId(), goal.getId(),
                            "Congratulations! You have completed your goal to save for " + goal.getName() + "!");
                }

        }
    }

    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    public List<Goal> getGoalsByUserId(String userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);

        if (goals.isEmpty()) {
            throw new RuntimeException("No goals found!");
        }
        return goals;
    }

    public Goal getGoalById(String id, String userId) {
        return goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Goal not found!"));
    }

    public Goal updateGoalById(String id, String userId, Goal updatedGoal) {
        Optional<Goal> goal = goalRepository.findByIdAndUserId(id, userId);
        Goal existingGoal = goal.orElseThrow(() -> new RuntimeException("Goal not found!"));

        existingGoal.setName(updatedGoal.getName());
        existingGoal.setTargetAmount(updatedGoal.getTargetAmount());
        existingGoal.setTargetDate(updatedGoal.getTargetDate());
        existingGoal.setCurrentAmount(updatedGoal.getCurrentAmount());
        existingGoal.setIncomeAllocationPercentage(updatedGoal.getIncomeAllocationPercentage());

        return goalRepository.save(existingGoal);
    }

    public Goal deleteGoalById(String id, String userId) {
        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Goal not found!"));

        goalRepository.deleteById(id);

        return goal;
    }

    public Goal updateProgressById(String id, String userId, double amount) {
        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Goal not found!"));

        if ("100%".equals(goal.getProgress())) {
            throw new RuntimeException("Goal is already completed");
        }

        goal.updateAmount(amount);
        goalRepository.save(goal);

        return goal;
    }
}
