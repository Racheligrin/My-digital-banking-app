package com.example.banking.repository;

import com.example.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // מאפשר לנו למצוא משתמש לפי שם המשתמש שלו (מעולה להתחברות בעתיד)
    Optional<User> findByUsername(String username);
}