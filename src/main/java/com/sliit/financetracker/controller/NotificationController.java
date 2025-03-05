package com.sliit.financetracker.controller;

import com.sliit.financetracker.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(notificationService
                .getNotifications(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable String id) {

        return ResponseEntity.status(HttpStatus.OK).body(notificationService
                .getNotificationById(id, userDetails.getUsername()));
    }
}
