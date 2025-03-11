package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.service.AuthService;
import com.sliit.financetracker.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid User user) {
        try {
            authService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try{
            User authenticatedUser = authService.authenticate(user.getEmail(), user.getPassword());
            String jwtToken = jwtService.generateToken(authenticatedUser);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("expiresIn", jwtService.getExpirationTime());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}