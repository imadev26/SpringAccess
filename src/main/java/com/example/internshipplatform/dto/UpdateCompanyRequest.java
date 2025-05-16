package com.example.internshipplatform.dto;

import lombok.Data;

@Data
public class UpdateCompanyRequest {
    private String name;
    private String email;
    private String address;
    private String contact;
    // Add more fields that should be editable
    private String description;
    private String industry;
    private String website;
} 