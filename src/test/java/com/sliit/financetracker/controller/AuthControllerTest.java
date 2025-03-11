package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.service.AuthService;
import com.sliit.financetracker.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // Test signup with valid data
    @Test
    void testSignupValidUser() throws Exception {
        User user = new User("testuser","test@example.com", "password123");

        doNothing().when(authService).createUser(user); // assuming createUser returns true on success

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType("application/json")
                        .content("{\"username\":\"testuser\", \"email\":\"test@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isCreated()) // Expecting 201 Created status
                .andExpect(content().string("User registered successfully!"));
    }

    // Test signup with invalid data
    @Test
    void testSignupInvalidUser() throws Exception {
        User user = new User("", "", ""); // Invalid user (empty email and password)

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"\", \"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    // Test login with valid credentials
    @Test
    void testLoginValidUser() throws Exception {
        User user = new User("testuser", "test@example.com", "password123");
        String jwtToken = "sample-jwt-token";

        when(authService.authenticate(user.getEmail(), user.getPassword())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    // Test login with invalid credentials
    @Test
    void testLoginInvalidUser() throws Exception {
        User user = new User("testuser", "test@example.com", "wrongpassword");

        when(authService.authenticate(user.getEmail(), user.getPassword())).thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }
}

