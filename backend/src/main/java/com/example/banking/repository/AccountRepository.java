package com.example.banking.repository;

import com.example.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // פונקציה מותאמת אישית שתעזור לנו למצוא חשבון לפי מספר החשבון שלו (ולא לפי ה-ID)
    Optional<Account> findByAccountNumber(String accountNumber);
}