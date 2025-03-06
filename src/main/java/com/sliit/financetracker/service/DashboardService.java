package com.sliit.financetracker.service;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final FinancialReportService financialReportService;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public DashboardService(UserRepository userRepository,
                            GoalRepository goalRepository,
                            BudgetRepository budgetRepository,
                            TransactionRepository transactionRepository, FinancialReportService financialReportService,
                            RecurringTransactionRepository recurringTransactionRepository) {
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.financialReportService = financialReportService;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    public Map<String, Object> getDashboardData(User user, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> dashboardData = new HashMap<>();

        if (user.getRole().equals("USER")) {
            // Regular User Dashboard Data
            dashboardData.put("recurringTransactions", recurringTransactionRepository.findByUserId(user.getUsername()));
            dashboardData.put("transactions", transactionRepository.findByUserId(user.getUsername()));
            dashboardData.put("budgets", budgetRepository.findBudgetsByUserId(user.getUsername()));
            dashboardData.put("goals", goalRepository.findByUserId(user.getUsername()));
            dashboardData.put("financialReport", financialReportService.generateReport
                    (user.getUsername(), startDate, endDate, null, null));
        } else if (user.getRole().equals("ADMIN")) {
            // Admin Dashboard Data
            List<User> users = userRepository.findAll();

            List<Map<String, Object>> userDetails = users.stream()
                    .filter(u -> !u.getRole().equals("ADMIN")) // Exclude other admins
                    .map(u -> Map.of(
                            "username", u.getUsername(),
                            "email", u.getEmail(),
                            "role", u.getRole(),
                            "currency", u.getCurrency(),
                            "transactions", transactionRepository.findByUserId(u.getUsername()),
                            "budgets", budgetRepository.findBudgetsByUserId(u.getUsername()),
                            "goals", goalRepository.findByUserId(u.getUsername()),
                            "financialReport", financialReportService.generateReport
                                    (u.getUsername(), startDate, endDate, null, null))).toList();

            dashboardData.put("Total users", userRepository.count());
            dashboardData.put("Users", userDetails);
        }

        return dashboardData;
    }
}
