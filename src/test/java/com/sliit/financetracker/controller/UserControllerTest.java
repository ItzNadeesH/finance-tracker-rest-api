package com.sliit.financetracker.controller;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.repository.UserRepository;
import com.sliit.financetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        user = new User("user", "user@gmail.com", "password");
        user.setUsername(username);
        user.setPassword("password123");
        user.setEmail("test@example.com");
    }

    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername(username);

        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    void testGetUserByUsernameThrowsExceptionIfNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername(username);
        });

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    void testUpdateUserByUsername() {
        User updatedUser = new User("user", "updated@example.com", "newPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserByUsername(username, updatedUser);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void testUpdateUserByUsernameThrowsExceptionIfNotFound() {
        User updatedUser = new User("user", "updated@example.com", "newPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserByUsername(username, updatedUser);
        });

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    void testDeleteUserByUsername() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User deletedUser = userService.deleteUserByUsername(username);

        verify(userRepository, times(1)).delete(user);
        assertNotNull(deletedUser);
        assertEquals(username, deletedUser.getUsername());
    }

    @Test
    void testDeleteUserByUsernameThrowsExceptionIfNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUserByUsername(username);
        });

        assertEquals("User not found!", exception.getMessage());
    }


}

