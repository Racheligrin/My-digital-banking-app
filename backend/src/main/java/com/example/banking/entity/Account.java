package com.example.banking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String accountNumber;
    private double balance;

    // 1. במקום String ownerName - אנחנו מקשרים את החשבון למשתמש אמיתי
    @ManyToOne
    @JoinColumn(name = "user_id") // יוצר עמודת מפתח זר בטבלה שמקשרת למשתמש
    private User user;

    // 2. הוספת קשר להיסטוריית הפעולות (תנועות) של החשבון
    // נשתמש ב-MappedBy כדי להגיד שבישות Transaction יש שדה שמקשר חזרה לחשבון הזה
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}