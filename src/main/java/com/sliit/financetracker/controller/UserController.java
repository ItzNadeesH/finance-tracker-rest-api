package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.service.CurrencyService;
import com.sliit.financetracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final CurrencyService currencyService;
    private final UserService userService;

    public UserController(CurrencyService currencyService, UserService userService) {
        this.currencyService = currencyService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(userService
                .getUserByUsername(userDetails.getUsername()));
    }

    @PutMapping("/")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody @Valid User updatedUser) {
        return ResponseEntity.status(HttpStatus.OK).body(userService
                .updateUserByUsername(userDetails.getUsername(), updatedUser));
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(userService
                .deleteUserByUsername(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<?> changeCurrency(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam String currency) {
        return ResponseEntity.status(HttpStatus.OK).body(currencyService
                .changeCurrency(userDetails.getUsername(), currency));
    }
}
