package com.example.banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter 
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operationType; 
    private double amount;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
