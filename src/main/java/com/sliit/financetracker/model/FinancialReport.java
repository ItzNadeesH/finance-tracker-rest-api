package com.sliit.financetracker.model;

import java.time.LocalDate;
import java.util.Map;

public class FinancialReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalIncome;
    private double totalExpenses;
    private Map<String, Double> expensesByCategory;
    private Map<String, Double> incomesByCategory;

    public FinancialReport(LocalDate startDate,
                           LocalDate endDate,
                           double totalIncome,
                           double totalExpenses,
                           Map<String, Double> expensesByCategory,
                           Map<String, Double> incomesByCategory) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.expensesByCategory = expensesByCategory;
        this.incomesByCategory = incomesByCategory;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Map<String, Double> getExpensesByCategory() {
        return expensesByCategory;
    }

    public void setExpensesByCategory(Map<String, Double> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }

    public Map<String, Double> getIncomesByCategory() {
        return incomesByCategory;
    }

    public void setIncomesByCategory(Map<String, Double> incomesByCategory) {
        this.incomesByCategory = incomesByCategory;
    }
}
