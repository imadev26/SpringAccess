package com.example.internshipplatform.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "companies")
public class Company extends User {
    private String name;
    private String address;
    private String contact;
} 