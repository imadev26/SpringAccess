package com.example.internshipplatform.controller;

import com.example.internshipplatform.model.Contract;
import com.example.internshipplatform.repository.ContractRepository;
import com.example.internshipplatform.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    @Autowired
    private ContractRepository contractRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY', 'STUDENT', 'ADMIN')")
    public ResponseEntity<?> getContract(@PathVariable String id) {
        try {
            return contractRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to get contract: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> createContract(@RequestBody Contract contract, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            // Set creation dates
            contract.setCreatedAt(LocalDateTime.now());
            contract.setUpdatedAt(LocalDateTime.now());
            contract.setDateSigned(LocalDateTime.now());
            
            Contract savedContract = contractRepository.save(contract);
            return ResponseEntity.ok(savedContract);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create contract: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 