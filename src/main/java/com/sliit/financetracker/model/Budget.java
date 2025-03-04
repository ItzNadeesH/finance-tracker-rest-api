package com.sliit.financetracker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "budgets")
public class Budget {

    @Id
    private String id;

    private String userId; // Reference to the user who owns the budget

    @Positive(message = "Budget amount must be positive")
    private double amount; // Budgeted amount

    @NotBlank(message = "Budget type is required")
    @Pattern(regexp = "monthly|category", message = "Type must be 'monthly' or 'category'")
    private String type; // Defines if it's a 'monthly' or 'category' budget

    private String category; // Optional: Only required for category-specific budgets

    private LocalDate startDate; // Optional: Only required for monthly budgets

    private LocalDate renewDate; // Optional: Only required for monthly budgets

    private double limit; // Budget limit for alerts

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getRenewDate() {
        return renewDate;
    }

    public void setRenewDate(LocalDate renewDate) {
        this.renewDate = renewDate;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

}
