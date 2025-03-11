package com.sliit.financetracker.service;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");

        authService.createUser(testUser);

        assertEquals("encodedPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.createUser(testUser));
        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.createUser(testUser));
        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        User authenticatedUser = authService.authenticate(testUser.getEmail(), "password");

        assertNotNull(authenticatedUser);
        assertEquals(testUser.getEmail(), authenticatedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.authenticate("invalid@example.com", "password"));
        assertEquals("Invalid email address", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForIncorrectPassword() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        doThrow(new BadCredentialsException("Incorrect password")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.authenticate(testUser.getEmail(), "wrongPassword"));
        assertEquals("Incorrect password", exception.getMessage());
    }

    @Test
    void shouldReturnAuthenticatedUsername() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        String username = authService.getAuthenticatedUsername();

        assertEquals("testuser", username);
    }

    @Test
    void shouldReturnNullWhenNoUserAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        String username = authService.getAuthenticatedUsername();

        assertNull(username);
    }
}

