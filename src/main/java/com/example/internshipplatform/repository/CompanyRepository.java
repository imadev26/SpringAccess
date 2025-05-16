package com.example.internshipplatform.repository;

import com.example.internshipplatform.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
 
public interface CompanyRepository extends MongoRepository<Company, String> {
} 