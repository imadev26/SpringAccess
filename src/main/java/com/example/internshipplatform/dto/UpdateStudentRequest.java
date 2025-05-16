package com.example.internshipplatform.dto;

import lombok.Data;

@Data
public class UpdateStudentRequest {
    private String name;
    private String email;
    // Add more fields that should be editable
    private String bio;
    private String[] skills;
    private String education;
    private String phoneNumber;
} 