package com.sliit.financetracker.controller;

import com.sliit.financetracker.service.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final CurrencyService currencyService;

    public UserController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    public ResponseEntity<?> changeCurrency(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam String currency) {
        return ResponseEntity.status(HttpStatus.OK).body(currencyService
                .changeCurrency(userDetails.getUsername(), currency));
    }
}
