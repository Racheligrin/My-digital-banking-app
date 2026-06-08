package com.example.banking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private String operationType;
    private double amount;
    private LocalDateTime timestamp;
}