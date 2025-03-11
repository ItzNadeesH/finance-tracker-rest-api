package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal User user,
                                          @RequestParam LocalDate startDate,
                                          @RequestParam LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK).body
                (dashboardService.getDashboardData(user, startDate, endDate));
    }
}
