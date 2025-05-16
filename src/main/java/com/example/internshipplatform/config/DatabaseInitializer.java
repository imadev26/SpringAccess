package com.example.internshipplatform.config;

import com.example.internshipplatform.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component // Enabled to initialize the database with test data
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createIndexes();
        insertTestData();
    }

    private void createIndexes() {
        // Create indexes for User collection
        IndexOperations userIndexOps = mongoTemplate.indexOps("users");
        IndexResolver userResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
        userResolver.resolveIndexFor(User.class).forEach(userIndexOps::ensureIndex);

        // Create indexes for Student collection
        IndexOperations studentIndexOps = mongoTemplate.indexOps("students");
        IndexResolver studentResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
        studentResolver.resolveIndexFor(Student.class).forEach(studentIndexOps::ensureIndex);

        // Create indexes for Company collection
        IndexOperations companyIndexOps = mongoTemplate.indexOps("companies");
        IndexResolver companyResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
        companyResolver.resolveIndexFor(Company.class).forEach(companyIndexOps::ensureIndex);

        // Create indexes for Offer collection
        IndexOperations offerIndexOps = mongoTemplate.indexOps("offers");
        IndexResolver offerResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
        offerResolver.resolveIndexFor(Offer.class).forEach(offerIndexOps::ensureIndex);

        // Create indexes for Application collection
        IndexOperations applicationIndexOps = mongoTemplate.indexOps("applications");
        IndexResolver applicationResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
        applicationResolver.resolveIndexFor(Application.class).forEach(applicationIndexOps::ensureIndex);

        // Create indexes for Contract collection
        IndexOperations contractIndexOps = mongoTemplate.indexOps("contracts");
        IndexResolver contractResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
        contractResolver.resolveIndexFor(Contract.class).forEach(contractIndexOps::ensureIndex);
    }

    private void insertTestData() {
        // Clear existing data
        mongoTemplate.dropCollection("users");
        mongoTemplate.dropCollection("students");
        mongoTemplate.dropCollection("companies");
        mongoTemplate.dropCollection("offers");
        mongoTemplate.dropCollection("applications");
        mongoTemplate.dropCollection("contracts");

        // Students
        Student student1 = new Student();
        student1.setName("Alice Martin");
        student1.setEmail("alice@student.com");
        student1.setPassword(passwordEncoder.encode("password"));
        student1.setRole("STUDENT");

        Student student2 = new Student();
        student2.setName("Bob Dupont");
        student2.setEmail("bob@student.com");
        student2.setPassword(passwordEncoder.encode("password"));
        student2.setRole("STUDENT");

        mongoTemplate.save(student1);
        mongoTemplate.save(student2);

        // Companies
        Company company1 = new Company();
        company1.setName("TechCorp");
        company1.setAddress("123 Tech Street");
        company1.setContact("contact@techcorp.com");
        company1.setEmail("hr@techcorp.com");
        company1.setPassword(passwordEncoder.encode("password"));
        company1.setRole("COMPANY");

        Company company2 = new Company();
        company2.setName("InnovateX");
        company2.setAddress("456 Innovation Ave");
        company2.setContact("info@innovatex.com");
        company2.setEmail("hr@innovatex.com");
        company2.setPassword(passwordEncoder.encode("password"));
        company2.setRole("COMPANY");

        mongoTemplate.save(company1);
        mongoTemplate.save(company2);

        // Offers
        Offer offer1 = new Offer();
        offer1.setTitle("Java Developer Intern");
        offer1.setDescription("Work on backend systems.");
        offer1.setSector("IT");
        offer1.setLocation("Paris");
        offer1.setDuration("6 months");
        offer1.setRequirements("Java, Spring Boot");
        offer1.setCompanyId(company1.getId());
        offer1.setCreatedAt(LocalDateTime.now());
        offer1.setUpdatedAt(LocalDateTime.now());

        Offer offer2 = new Offer();
        offer2.setTitle("Frontend React Intern");
        offer2.setDescription("Develop modern UIs.");
        offer2.setSector("IT");
        offer2.setLocation("Lyon");
        offer2.setDuration("4 months");
        offer2.setRequirements("React, CSS");
        offer2.setCompanyId(company2.getId());
        offer2.setCreatedAt(LocalDateTime.now());
        offer2.setUpdatedAt(LocalDateTime.now());

        mongoTemplate.save(offer1);
        mongoTemplate.save(offer2);

        // Applications
        Application app1 = new Application();
        app1.setStudentId(student1.getId());
        app1.setOfferId(offer1.getId());
        app1.setCv("alice_cv.pdf");
        app1.setCoverLetter("alice_cover_letter.pdf");
        app1.setStatus("SUBMITTED");
        app1.setDateSubmitted(LocalDateTime.now());
        app1.setLastUpdated(LocalDateTime.now());

        Application app2 = new Application();
        app2.setStudentId(student2.getId());
        app2.setOfferId(offer2.getId());
        app2.setCv("bob_cv.pdf");
        app2.setCoverLetter("bob_cover_letter.pdf");
        app2.setStatus("SUBMITTED");
        app2.setDateSubmitted(LocalDateTime.now());
        app2.setLastUpdated(LocalDateTime.now());

        mongoTemplate.save(app1);
        mongoTemplate.save(app2);

        // Contracts
        Contract contract1 = new Contract();
        contract1.setApplicationId(app1.getId());
        contract1.setFile("contract_alice.pdf");
        contract1.setDateSigned(LocalDateTime.now());
        contract1.setCreatedAt(LocalDateTime.now());
        contract1.setUpdatedAt(LocalDateTime.now());

        mongoTemplate.save(contract1);
    }
} 