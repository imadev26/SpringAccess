package com.example.internshipplatform.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "applications")
public class Application {
    @Id
    private String id;
    private String studentId;
    private String offerId;
    private String cv;
    private String coverLetter;
    private String status;
    private LocalDateTime dateSubmitted;
    private LocalDateTime lastUpdated;
} 