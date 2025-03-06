package com.sliit.financetracker.service;

import com.sliit.financetracker.model.User;
import com.sliit.financetracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public User updateUserByUsername(String username, User updatedUser) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found!"));

        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        user.setEmail(updatedUser.getEmail());

        return userRepository.save(user);
    }

    public User deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found!"));

        userRepository.delete(user);
        return user;
    }
}
