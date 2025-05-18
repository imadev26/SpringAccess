package com.example.internshipplatform.controller;

import com.example.internshipplatform.dto.CompanyDashboardStatsDTO;
import com.example.internshipplatform.model.Application;
import com.example.internshipplatform.model.Offer;
import com.example.internshipplatform.repository.ApplicationRepository;
import com.example.internshipplatform.repository.OfferRepository;
import com.example.internshipplatform.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping("/company/stats")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyDashboardStatsDTO> getCompanyDashboardStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        String companyId = userPrincipal.getId();
        
        // Get total offers for the company
        List<Offer> companyOffers = offerRepository.findByCompanyId(companyId);
        long totalOffers = companyOffers.size();
        
        // Get all applications for company's offers
        List<Application> allApplications = companyOffers.stream()
                .flatMap(offer -> applicationRepository.findByOfferId(offer.getId()).stream())
                .collect(Collectors.toList());
        
        long totalApplications = allApplications.size();
        
        // Count applications by status
        Map<String, Long> applicationsByStatus = allApplications.stream()
                .collect(Collectors.groupingBy(
                        Application::getStatus,
                        Collectors.counting()
                ));
        
        // Ensure all statuses are present in the map
        String[] allStatuses = {"SUBMITTED", "UNDER_REVIEW", "INTERVIEW_SCHEDULED", "ACCEPTED", "REJECTED"};
        for (String status : allStatuses) {
            applicationsByStatus.putIfAbsent(status, 0L);
        }
        
        CompanyDashboardStatsDTO stats = new CompanyDashboardStatsDTO(
                totalOffers,
                totalApplications,
                applicationsByStatus
        );
        
        return ResponseEntity.ok(stats);
    }
} 