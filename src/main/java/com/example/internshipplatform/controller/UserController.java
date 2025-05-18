package com.example.internshipplatform.controller;

import com.example.internshipplatform.dto.UpdateCompanyRequest;
import com.example.internshipplatform.dto.UpdateStudentRequest;
import com.example.internshipplatform.model.Company;
import com.example.internshipplatform.model.Student;
import com.example.internshipplatform.repository.CompanyRepository;
import com.example.internshipplatform.repository.StudentRepository;
import com.example.internshipplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/students/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('COMPANY')")
    public ResponseEntity<Student> getStudentDetails(@PathVariable String id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/companies/{id}")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    public ResponseEntity<Company> getCompanyDetails(@PathVariable String id) {
        return companyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PutMapping("/students/{id}")
    @PreAuthorize("hasRole('STUDENT') and #id == authentication.principal.id or hasRole('COMPANY')")
    public ResponseEntity<?> updateStudentProfile(
            @PathVariable String id,
            @RequestBody UpdateStudentRequest updateRequest) {
        try {
            Optional<Student> studentOptional = studentRepository.findById(id);
            
            if (studentOptional.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Student not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Student student = studentOptional.get();
            
            // Update fields if they're not null
            if (updateRequest.getName() != null) {
                student.setName(updateRequest.getName());
            }
            
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(student.getEmail())) {
                // Check if email is already taken
                if (userRepository.findByEmail(updateRequest.getEmail()).isPresent()) {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Email is already taken");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                student.setEmail(updateRequest.getEmail());
            }
            
            // Handle additional fields
            // These fields would need to be added to the Student model
            // Example: if (updateRequest.getBio() != null) student.setBio(updateRequest.getBio());
            
            // Save updated student
            Student updatedStudent = studentRepository.save(student);
            // If email changed, also update in users collection
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(student.getEmail())) {
                userRepository.save(student);
            }
            
            return ResponseEntity.ok(updatedStudent);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/companies/{id}")
    @PreAuthorize("hasRole('COMPANY') and #id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> updateCompanyProfile(
            @PathVariable String id,
            @RequestBody UpdateCompanyRequest updateRequest) {
        try {
            Optional<Company> companyOptional = companyRepository.findById(id);
            
            if (companyOptional.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Company not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Company company = companyOptional.get();
            
            // Update fields if they're not null
            if (updateRequest.getName() != null) {
                company.setName(updateRequest.getName());
            }
            
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(company.getEmail())) {
                // Check if email is already taken
                if (userRepository.findByEmail(updateRequest.getEmail()).isPresent()) {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Email is already taken");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                company.setEmail(updateRequest.getEmail());
            }
            
            if (updateRequest.getAddress() != null) {
                company.setAddress(updateRequest.getAddress());
            }
            
            if (updateRequest.getContact() != null) {
                company.setContact(updateRequest.getContact());
            }
            
            // Handle additional fields
            // These fields would need to be added to the Company model
            // Example: if (updateRequest.getDescription() != null) company.setDescription(updateRequest.getDescription());
            
            // Save updated company
            Company updatedCompany = companyRepository.save(company);
            // If email changed, also update in users collection
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(company.getEmail())) {
                userRepository.save(company);
            }
            
            return ResponseEntity.ok(updatedCompany);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 