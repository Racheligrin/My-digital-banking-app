package com.example.banking.dto;

import lombok.Data;
import java.util.List;

@Data
public class AccountDTO {
    private String accountNumber;
    private double balance;
    
    // פרטי בעל החשבון (במקום ישות User מלאה עם סיסמה, נחשוף רק מה שצריך)
    private String username;
    private String email;
    
    // רשימה של התנועות האחרונות בחשבון (ניצור לה DTO ייעודי מיד)
    private List<TransactionDTO> transactions;
}