package com.example.internshipplatform.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "contracts")
public class Contract {
    @Id
    private String id;
    private String applicationId;
    private String file;
    private LocalDateTime dateSigned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 