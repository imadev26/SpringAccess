package com.example.internshipplatform.controller;

import com.example.internshipplatform.model.Application;
import com.example.internshipplatform.model.Offer;
import com.example.internshipplatform.repository.ApplicationRepository;
import com.example.internshipplatform.repository.OfferRepository;
import com.example.internshipplatform.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private OfferRepository offerRepository;

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentApplications(
            @PathVariable String studentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        System.out.println("Getting applications for student ID: " + studentId);
        System.out.println("Authenticated user ID: " + userPrincipal.getId());
        System.out.println("User authorities: " + userPrincipal.getAuthorities());
        
        // Ensure student can only access their own applications
        if (!studentId.equals(userPrincipal.getId())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "You can only access your own applications");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        List<Application> applications = applicationRepository.findByStudentId(studentId);
        System.out.println("Found " + applications.size() + " applications");
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/offers/{offerId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> getOfferApplications(
            @PathVariable String offerId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        // Check if the offer belongs to the company
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        
        if (offerOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Offer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Offer offer = offerOpt.get();
        if (!offer.getCompanyId().equals(userPrincipal.getId())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "You can only view applications for your own offers");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        List<Application> applications = applicationRepository.findByOfferId(offerId);
        return ResponseEntity.ok(applications);
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createApplication(
            @RequestBody Application application,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            // Set the student ID from the authenticated user
            application.setStudentId(userPrincipal.getId());
            application.setDateSubmitted(LocalDateTime.now());
            application.setLastUpdated(LocalDateTime.now());
            application.setStatus("SUBMITTED");
            
            Application savedApplication = applicationRepository.save(application);
            return ResponseEntity.ok(savedApplication);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable String id,
            @RequestParam String status,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<Application> applicationOpt = applicationRepository.findById(id);
            
            if (applicationOpt.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Application not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Application application = applicationOpt.get();
            
            // Check if the application is for an offer from this company
            Optional<Offer> offerOpt = offerRepository.findById(application.getOfferId());
            
            if (offerOpt.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Offer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Offer offer = offerOpt.get();
            if (!offer.getCompanyId().equals(userPrincipal.getId())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "You can only update applications for your own offers");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            application.setStatus(status);
            application.setLastUpdated(LocalDateTime.now());
            
            Application updatedApplication = applicationRepository.save(application);
            return ResponseEntity.ok(updatedApplication);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update application status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // GET application by ID - Protected: COMPANY (own offer's applications) or ADMIN only
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    public ResponseEntity<?> getApplicationById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<Application> applicationOpt = applicationRepository.findById(id);

            if (applicationOpt.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Application not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Application application = applicationOpt.get();

            // Check if the authenticated user is a COMPANY and owns the offer associated with this application
            if (userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COMPANY"))) {
                 Optional<Offer> offerOpt = offerRepository.findById(application.getOfferId());

                 if (offerOpt.isEmpty()) {
                     // This should ideally not happen if data is consistent, but handle defensively
                     Map<String, String> response = new HashMap<>();
                     response.put("error", "Associated offer not found");
                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                 }

                 Offer offer = offerOpt.get();

                 if (!offer.getCompanyId().equals(userPrincipal.getId())) {
                     Map<String, String> response = new HashMap<>();
                     response.put("error", "You do not have permission to access this application");
                     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                 }
            }
            // Admins are already authorized by @PreAuthorize

            return ResponseEntity.ok(application);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to retrieve application details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 