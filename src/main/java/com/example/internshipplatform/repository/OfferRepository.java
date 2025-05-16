package com.example.internshipplatform.repository;

import com.example.internshipplatform.model.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OfferRepository extends MongoRepository<Offer, String> {
    List<Offer> findByCompanyId(String companyId);
} 