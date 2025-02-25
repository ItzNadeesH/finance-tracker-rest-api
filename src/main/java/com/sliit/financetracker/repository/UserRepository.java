package com.sliit.financetracker.repository;

import com.sliit.financetracker.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
