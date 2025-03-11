package com.sliit.financetracker.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "goals")
public class Goal {

    @Id
    private String id;

    private String userId;

    @NotBlank(message = "Goal name is required")
    private String name;

    @Positive(message = "Target amount amount must be positive")
    private double targetAmount;

    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    private double currentAmount;

    private String progress;

    private double incomeAllocationPercentage;

    private LocalDate date = LocalDate.now();

    public Goal(String name, double targetAmount, LocalDate targetDate) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.currentAmount = 0;
        this.progress = "0%";
    }

    public void updateAmount(double amount) {
        if (this.currentAmount + amount > this.targetAmount) {
            this.currentAmount = this.targetAmount;
            this.progress = "100%";
        } else {
            this.currentAmount += amount;
            this.progress = calculateProgress();
        }
    }

    public String calculateProgress() {
        return String.format("%.2f%%", (currentAmount / targetAmount) * 100);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public double getIncomeAllocationPercentage() {
        return incomeAllocationPercentage;
    }

    public void setIncomeAllocationPercentage(double incomeAllocationPercentage) {
        this.incomeAllocationPercentage = incomeAllocationPercentage;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
