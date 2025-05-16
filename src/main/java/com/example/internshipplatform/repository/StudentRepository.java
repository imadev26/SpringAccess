package com.example.internshipplatform.repository;

import com.example.internshipplatform.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
 
public interface StudentRepository extends MongoRepository<Student, String> {
} 