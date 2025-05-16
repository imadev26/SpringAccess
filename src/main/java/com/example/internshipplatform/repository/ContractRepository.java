package com.example.internshipplatform.repository;

import com.example.internshipplatform.model.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContractRepository extends MongoRepository<Contract, String> {
    Optional<Contract> findByApplicationId(String applicationId);
} 