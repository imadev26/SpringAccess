package com.example.internshipplatform.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "offers")
public class Offer {
    @Id
    private String id;
    private String title;
    private String description;
    private String sector;
    private String location;
    private String duration;
    private String requirements;
    private String companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 