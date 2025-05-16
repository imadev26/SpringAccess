package com.example.internshipplatform.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String role; // STUDENT or COMPANY
    private String name;
    // Optional fields for company
    private String address;
    private String contact;
} 