package com.example.internshipplatform.controller;

import com.example.internshipplatform.dto.AuthResponse;
import com.example.internshipplatform.dto.LoginRequest;
import com.example.internshipplatform.dto.RegisterRequest;
import com.example.internshipplatform.model.Company;
import com.example.internshipplatform.model.Student;
import com.example.internshipplatform.model.User;
import com.example.internshipplatform.repository.CompanyRepository;
import com.example.internshipplatform.repository.StudentRepository;
import com.example.internshipplatform.repository.UserRepository;
import com.example.internshipplatform.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("Register request received: " + registerRequest.getEmail() + ", role: " + registerRequest.getRole());
            
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                System.out.println("Email already taken: " + registerRequest.getEmail());
                Map<String, String> response = new HashMap<>();
                response.put("error", "Email is already taken!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            User user;
            if ("STUDENT".equals(registerRequest.getRole())) {
                System.out.println("Creating new student user");
                Student student = new Student();
                student.setName(registerRequest.getName());
                student.setEmail(registerRequest.getEmail());
                student.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                student.setRole("STUDENT");
                user = studentRepository.save(student);
                userRepository.save(student);
            } else if ("COMPANY".equals(registerRequest.getRole())) {
                System.out.println("Creating new company user");
                Company company = new Company();
                company.setName(registerRequest.getName());
                company.setEmail(registerRequest.getEmail());
                company.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                company.setRole("COMPANY");
                company.setAddress(registerRequest.getAddress());
                company.setContact(registerRequest.getContact());
                user = companyRepository.save(company);
                userRepository.save(company);
            } else {
                System.out.println("Invalid role: " + registerRequest.getRole());
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid role! Must be either STUDENT or COMPANY");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            System.out.println("User registered successfully with ID: " + user.getId() + ", role: " + user.getRole());
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "User registered successfully! Please login to get your token.");
            responseMap.put("role", user.getRole());
            responseMap.put("userId", user.getId());
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login request received: " + loginRequest.getEmail());
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            System.out.println("Login successful for user: " + user.getId() + ", role: " + user.getRole());
            System.out.println("Generated token: " + jwt);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("token", jwt);
            response.put("role", user.getRole());
            response.put("userId", user.getId());
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.out.println("Invalid credentials for: " + loginRequest.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 