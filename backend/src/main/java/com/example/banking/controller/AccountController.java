package com.example.banking.controller;

import com.example.banking.dto.AccountDTO;
import com.example.banking.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*") // מאפשר ל-React להתחבר לשרת בלי חסימות אבטחה של דפדפנים (CORS)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // 1. יצירת חשבון חדש
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        AccountDTO created = accountService.createAccount(accountDTO);
        return ResponseEntity.ok(created);
    }

    // 2. קבלת פרטי חשבון לפי מספר חשבון
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable String accountNumber) {
        AccountDTO account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    // 3. נקודת קצה לפונקציה מעניינת 1: העברת כספים
    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(
            @RequestParam String sourceAccountNumber,
            @RequestParam String destAccountNumber,
            @RequestParam double amount) {
        
        accountService.transferMoney(sourceAccountNumber, destAccountNumber, amount);
        return ResponseEntity.ok("ההעברה בוצעה בהצלחה!");
    }

    // 4. נקודת קצה לפונקציה מעניינת 2: הפקדה או משיכה
    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountDTO> updateBalance(
            @PathVariable String accountNumber,
            @RequestParam String operationType,
            @RequestParam double amount) {
        
        AccountDTO updated = accountService.updateBalance(accountNumber, operationType, amount);
        return ResponseEntity.ok(updated);
    }

    // 5. נקודת קצה לפונקציה מעניינת 3: בקשת הלוואה אוטומטית
    @PostMapping("/{accountNumber}/loan")
    public ResponseEntity<AccountDTO> requestLoan(
            @PathVariable String accountNumber,
            @RequestParam double loanAmount) {
        
        AccountDTO updated = accountService.requestLoan(accountNumber, loanAmount);
        return ResponseEntity.ok(updated);
    }
}