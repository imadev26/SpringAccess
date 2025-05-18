package com.example.internshipplatform.controller;

import com.example.internshipplatform.model.Offer;
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
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/offers")
@CrossOrigin(origins = "*")
public class OfferController {

    @Autowired
    private OfferRepository offerRepository;

    // GET all offers - Public endpoint
    @GetMapping
    public List<Offer> getAllOffers(
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String duration) {
        
        List<Offer> offers = offerRepository.findAll();
        
        if (sector != null) {
            offers = offers.stream()
                    .filter(offer -> offer.getSector().equals(sector))
                    .toList();
        }
        
        if (location != null) {
            offers = offers.stream()
                    .filter(offer -> offer.getLocation().equals(location))
                    .toList();
        }
        
        if (duration != null) {
            offers = offers.stream()
                    .filter(offer -> offer.getDuration().equals(duration))
                    .toList();
        }
        
        return offers;
    }

    // GET offer by ID - Public endpoint
    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable String id) {
        Optional<Offer> offer = offerRepository.findById(id);
        return offer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET offers by company ID - Protected: COMPANY (own offers) or ADMIN only
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('COMPANY') and #companyId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> getOffersByCompanyId(
            @PathVariable String companyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            // The @PreAuthorize annotation handles the authorization check.
            // We can directly fetch offers for the given companyId.
            List<Offer> offers = offerRepository.findByCompanyId(companyId);
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to retrieve offers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CREATE new offer - Protected: COMPANY only
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> createOffer(@RequestBody Offer offer, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            System.out.println("Creating offer with company ID: " + userPrincipal.getId());
            System.out.println("User authorities: " + userPrincipal.getAuthorities());
            
            // Set company ID from authenticated user
            offer.setCompanyId(userPrincipal.getId());
            offer.setCreatedAt(LocalDateTime.now());
            offer.setUpdatedAt(LocalDateTime.now());
            
            Offer savedOffer = offerRepository.save(offer);
            return ResponseEntity.ok(savedOffer);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create offer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // UPDATE offer - Protected: COMPANY only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> updateOffer(@PathVariable String id, @RequestBody Offer offerDetails, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            return offerRepository.findById(id)
                    .map(existingOffer -> {
                        // Check if the company owns this offer
                        if (!existingOffer.getCompanyId().equals(userPrincipal.getId())) {
                            Map<String, String> response = new HashMap<>();
                            response.put("error", "You don't have permission to update this offer");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                        
                        existingOffer.setTitle(offerDetails.getTitle());
                        existingOffer.setDescription(offerDetails.getDescription());
                        existingOffer.setSector(offerDetails.getSector());
                        existingOffer.setLocation(offerDetails.getLocation());
                        existingOffer.setDuration(offerDetails.getDuration());
                        existingOffer.setRequirements(offerDetails.getRequirements());
                        existingOffer.setUpdatedAt(LocalDateTime.now());
                        
                        return ResponseEntity.ok(offerRepository.save(existingOffer));
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update offer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // DELETE offer - Protected: COMPANY only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> deleteOffer(@PathVariable String id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            return offerRepository.findById(id)
                    .map(offer -> {
                        // Check if the company owns this offer
                        if (!offer.getCompanyId().equals(userPrincipal.getId())) {
                            Map<String, String> response = new HashMap<>();
                            response.put("error", "You don't have permission to delete this offer");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                        
                        offerRepository.delete(offer);
                        return ResponseEntity.ok().build();
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete offer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 