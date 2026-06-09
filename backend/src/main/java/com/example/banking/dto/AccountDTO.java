package com.example.banking.dto;

import lombok.Data;
import java.util.List;

@Data
public class AccountDTO {
    private String accountNumber;
    private double balance;
    
    private String username;
    private String email;
    
    private List<TransactionDTO> transactions;
}
