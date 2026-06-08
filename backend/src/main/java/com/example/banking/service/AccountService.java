package com.example.banking.service;

import com.example.banking.dto.AccountDTO;
import com.example.banking.dto.TransactionDTO;
import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.entity.User;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    // הזרקת כל ה-Repositories הרלוונטיים לתוך השירות
    public AccountService(AccountRepository accountRepository, 
                          UserRepository userRepository, 
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    // 1. יצירת חשבון חדש (כולל יצירת משתמש במערכת אם הוא לא קיים)
    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        // יוצרים משתמש חדש בבסיס הנתונים עבור בעל החשבון
        User user = new User();
        user.setUsername(accountDTO.getUsername());
        user.setEmail(accountDTO.getEmail());
        user.setRole("CLIENT");
        User savedUser = userRepository.save(user);

        // יוצרים את החשבון ומקשרים אותו למשתמש ששמרנו
        Account account = new Account();
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setBalance(accountDTO.getBalance());
        account.setUser(savedUser);

        Account savedAccount = accountRepository.save(account);
        return convertToDto(savedAccount);
    }

    // 2. קבלת פרטי חשבון לפי מספר חשבון (כולל כל היסטוריית התנועות שלו!)
    public AccountDTO getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("החשבון לא נמצא במערכת"));
        return convertToDto(account);
    }

    // =================================================================
    // פונקציה מעניינת 1: העברת כספים בין חשבונות + תיעוד הפעולה
    // =================================================================
    @Transactional 
    public void transferMoney(String sourceAccountNumber, String destAccountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("סכום ההעברה חייב להיות גדול מאפס");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new RuntimeException("חשבון המקור לא נמצא"));

        Account destAccount = accountRepository.findByAccountNumber(destAccountNumber)
                .orElseThrow(() -> new RuntimeException("חשבון היעד לא נמצא"));

        if (sourceAccount.getBalance() < amount) {
            throw new RuntimeException("אין מספיק יתרה בחשבון לביצוע ההעברה");
        }

        // עדכון יתרות
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destAccount.setBalance(destAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(destAccount);

        // תיעוד הפעולה בחשבון המקור (משיכה/העברה החוצה)
        createTransactionRecord(sourceAccount, "TRANSFER_OUT", amount);
        // תיעוד הפעולה בחשבון היעד (הפקדה/העברה פנימה)
        createTransactionRecord(destAccount, "TRANSFER_IN", amount);
    }

    // =================================================================
    // פונקציה מעניינת 2: הפקדה או משיכת מזומן + תיעוד הפעולה
    // =================================================================
    @Transactional
    public AccountDTO updateBalance(String accountNumber, String operationType, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("הסכום לפעולה חייב להיות גדול מאפס");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("החשבון לא נמצא"));

        if ("DEPOSIT".equalsIgnoreCase(operationType)) {
            account.setBalance(account.getBalance() + amount);
        } else if ("WITHDRAW".equalsIgnoreCase(operationType)) {
            if (account.getBalance() < amount) {
                throw new RuntimeException("משיכה נכשלה: אין מספיק יתרה בחשבון");
            }
            account.setBalance(account.getBalance() - amount);
        } else {
            throw new IllegalArgumentException("סוג פעולה לא מוכר. יש לבחור DEPOSIT או WITHDRAW");
        }

        Account updatedAccount = accountRepository.save(account);
        
        // שמירת הפעולה בהיסטוריית התנועות
        createTransactionRecord(updatedAccount, operationType.toUpperCase(), amount);
        
        return convertToDto(updatedAccount);
    }

    // =================================================================
    // פונקציה מעניינת 3: מערכת אישור הלוואות דיגיטלית אוטומטית + תיעוד
    // =================================================================
    @Transactional
    public AccountDTO requestLoan(String accountNumber, double loanAmount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("החשבון לא נמצא"));

        double maxAllowedLoan = account.getBalance() * 3;

        if (loanAmount > maxAllowedLoan) {
            throw new RuntimeException("בקשת ההלוואה נדחתה: סכום ההלוואה גבוה מדי ביחס ליתרת המשתמש");
        }

        account.setBalance(account.getBalance() + loanAmount);
        Account updatedAccount = accountRepository.save(account);
        
        // שמירת הפעולה בהיסטוריית התנועות
        createTransactionRecord(updatedAccount, "LOAN_APPROVED", loanAmount);
        
        return convertToDto(updatedAccount);
    }

    // פונקציית עזר פנימית ליצירת ושמירת תנועה בבסיס הנתונים
    private void createTransactionRecord(Account account, String type, double amount) {
        Transaction tx = new Transaction();
        tx.setOperationType(type);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        tx.setAccount(account);
        transactionRepository.save(tx);
    }

    // =================================================================
    // פונקציות מיפוי (Mapping) מורכבות ומלאות - דרישת חובה בפרויקט
    // =================================================================
    
    private AccountDTO convertToDto(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        
        // מיפוי נתוני המשתמש מתוך ישות המשתמש הקשורה
        if (account.getUser() != null) {
            dto.setUsername(account.getUser().getUsername());
            dto.setEmail(account.getUser().getEmail());
        }
        
        // מיפוי רשימת התנועות מתוך ישויות התנועה של החשבון
        if (account.getTransactions() != null) {
            List<TransactionDTO> txDtos = account.getTransactions().stream().map(tx -> {
                TransactionDTO txDto = new TransactionDTO();
                txDto.setOperationType(tx.getOperationType());
                txDto.setAmount(tx.getAmount());
                txDto.setTimestamp(tx.getTimestamp());
                return txDto;
            }).collect(Collectors.toList());
            dto.setTransactions(txDtos);
        } else {
            dto.setTransactions(new ArrayList<>());
        }
        
        return dto;
    }
}