package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.Goal;
import com.sliit.financetracker.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/goal")
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<?> getGoals(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService.getGoalsByUserId(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGoalById(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService.getGoalById(id, userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<?> createGoal(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody @Valid Goal goal) {
        goal.setUserId(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(goalService.createGoal(goal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable String id,
                                        @RequestBody @Valid Goal goal) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService
                .updateGoalById(id, userDetails.getUsername(), goal));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProgress(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable String id,
                                            @RequestParam double amount) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService
                .updateProgressById(id, userDetails.getUsername(), amount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService
                .deleteGoalById(id, userDetails.getUsername()));
    }
}
