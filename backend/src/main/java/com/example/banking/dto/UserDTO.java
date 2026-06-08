package com.example.banking.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String role; // "CLIENT" או "ADMIN"
}