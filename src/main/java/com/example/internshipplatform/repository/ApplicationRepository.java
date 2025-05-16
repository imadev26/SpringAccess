package com.example.internshipplatform.repository;

import com.example.internshipplatform.model.Application;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application, String> {
    List<Application> findByStudentId(String studentId);
    List<Application> findByOfferId(String offerId);
} 