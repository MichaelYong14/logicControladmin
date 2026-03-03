package com.caps.eteeapp.controller;

import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.service.ApplicantService;
import com.caps.eteeapp.service.CurriculumService;
import com.caps.eteeapp.model.Curriculum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/applicants")
public class ApplicantController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicantController.class);

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private CurriculumService curriculumService;

    @PostMapping("/register")
    public ResponseEntity<Applicant> registerApplicant(@RequestBody Applicant applicant) {
        Applicant registeredApplicant = applicantService.registerApplicant(applicant.getEmail(), applicant.getPassword());
        return ResponseEntity.ok(registeredApplicant);
    }
    
    @PostMapping("/register-complete")
    public ResponseEntity<?> registerCompleteApplicant(@RequestBody Applicant applicant) {
        logger.info("=== REGISTER COMPLETE REQUEST START ===");
        logger.info("Received complete registration request for email: {}", applicant.getEmail());
        
        try {
            Applicant registeredApplicant = applicantService.registerCompleteApplicant(applicant);
            
            if (registeredApplicant == null) {
                // Email already exists
                Map<String, String> response = new HashMap<>();
                response.put("message", "Email already exists");
                logger.warn("Registration failed - Email already exists: {}", applicant.getEmail());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create response with just the necessary information
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful!");
            response.put("applicantId", registeredApplicant.getApplicantId());
            
            logger.info("=== REGISTER COMPLETE REQUEST END - SUCCESS ===");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("=== REGISTER COMPLETE REQUEST END - ERROR ===");
            logger.error("Exception details:", e);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration failed. Please try again.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginApplicant(@RequestBody(required = false) Applicant loginRequest) {
        Map<String, Object> response = new HashMap<>();

        if (loginRequest == null) {
            response.put("message", "Request body is required");
            return ResponseEntity.badRequest().body(response);
        }

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.put("message", "Email and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        logger.info("Login attempt for email: {}", email); // do NOT log password

        try {
            Optional<Applicant> applicant = applicantService.loginApplicant(email, password);
            if (applicant.isPresent()) {
                Applicant loggedInApplicant = applicant.get();
                response.put("message", "Login successful!");
                response.put("applicantId", loggedInApplicant.getApplicantId());
                response.put("profileIncomplete",
                        loggedInApplicant.getFirstName() == null || loggedInApplicant.getLastName() == null);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            // log full stacktrace for debugging
            logger.error("Login error for email {}: {}", email, e.getMessage(), e);
            response.put("message", "Internal server error");

            // detect Postgres LOB auto-commit issue and provide a helpful hint
            Throwable cause = e;
            while (cause != null) {
                String msg = cause.getMessage();
                if (msg != null && msg.contains("Large Objects may not be used in auto-commit mode")) {
                    response.put("hint", "Database LOB access error (Postgres). Add 'spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true' to application.properties OR map LOB column to TEXT.");
                    break;
                }
                cause = cause.getCause();
            }

            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Applicant> updateApplicant(@RequestBody Applicant updatedApplicant) {
        if (updatedApplicant.getApplicantId() == null) {
            return ResponseEntity.status(400).body(null); // Bad Request if applicantId is missing
        }
        Optional<Applicant> applicant = applicantService.findApplicantById(updatedApplicant.getApplicantId());
        if (applicant.isPresent()) {
            Applicant updated = applicantService.updateApplicant(applicant.get(), updatedApplicant);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(404).body(null); // Not Found if applicantId does not exist
    }

    @PatchMapping("/{applicantId}/complete-profile")
    public ResponseEntity<Applicant> completeProfile(@PathVariable Long applicantId, @RequestBody Applicant updatedApplicant) {
        Optional<Applicant> applicant = applicantService.findApplicantById(applicantId);
        if (applicant.isPresent()) {
            Applicant updated = applicantService.updateApplicant(applicant.get(), updatedApplicant);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(404).body(null); // Not Found if applicantId does not exist
    }

    @GetMapping("/{id}")
    public ResponseEntity<Applicant> getApplicantById(@PathVariable Long id) {
        Optional<Applicant> applicant = applicantService.findApplicantById(id);
        if (applicant.isPresent()) {
            return ResponseEntity.ok(applicant.get());
        }
        return ResponseEntity.notFound().build(); // Return 404 if the applicant is not found
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        logger.info("=== FORGOT PASSWORD REQUEST START ===");
        logger.info("Raw request body: {}", request);
        
        String email = request.get("email");
        Map<String, String> response = new HashMap<>();

        logger.info("Extracted email from request: '{}'", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Email validation failed - email is null or empty");
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            logger.info("Calling applicantService.generatePasswordResetToken with email: '{}'", email);
            boolean tokenGenerated = applicantService.generatePasswordResetToken(email);
            logger.info("Service returned tokenGenerated: {}", tokenGenerated);
            
            // Always return a generic success message for security
            response.put("message", "If an account with this email exists, a password reset link has been sent.");
            logger.info("=== FORGOT PASSWORD REQUEST END - SUCCESS ===");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("=== FORGOT PASSWORD REQUEST END - ERROR ===");
            logger.error("Exception details:", e);
            
            // Still return the same generic message
            response.put("message", "If an account with this email exists, a password reset link has been sent.");
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        logger.info("=== DIRECT RESET PASSWORD REQUEST START ===");
        logger.info("Raw request body: {}", request);
        
        String email = request.get("email");
        String newPassword = request.get("password");
        
        Map<String, String> response = new HashMap<>();

        logger.info("Extracted - email: '{}', password length: {}", 
                   email, newPassword != null ? newPassword.length() : "null");

        if (email != null && !email.trim().isEmpty()) {
            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.warn("Email-based reset: password is missing");
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            try {
                logger.info("Calling applicantService.resetPasswordDirectly with email");
                boolean passwordReset = applicantService.resetPasswordDirectly(email, newPassword);
                logger.info("Service returned passwordReset: {}", passwordReset);
                
                if (passwordReset) {
                    response.put("message", "Password has been reset successfully");
                    logger.info("=== RESET PASSWORD REQUEST END - SUCCESS ===");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("message", "Email not found or unable to reset password");
                    logger.info("=== RESET PASSWORD REQUEST END - EMAIL NOT FOUND ===");
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                logger.error("=== RESET PASSWORD REQUEST END - ERROR ===");
                logger.error("Exception details:", e);
                response.put("message", "An error occurred while resetting your password. Please try again.");
                return ResponseEntity.status(500).body(response);
            }
        } else {
            logger.warn("Validation failed - email not provided");
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Remove token-related endpoints that we're not using
    
    // Keep the direct reset endpoint with the PutMapping as an alias
    @PostMapping("/reset-password-direct")
    public ResponseEntity<Map<String, String>> resetPasswordDirect(@RequestBody Map<String, String> request) {
        // Just delegate to the main reset-password endpoint
        return resetPassword(request);
    }

    @PostMapping("/{applicantId}/create-curriculum-record")
    public ResponseEntity<Map<String, String>> createCurriculumRecord(
            @PathVariable Long applicantId, 
            @RequestParam Long curriculumId) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            Optional<Applicant> applicantOpt = applicantService.findApplicantById(applicantId);
            if (!applicantOpt.isPresent()) {
                response.put("message", "Applicant not found");
                return ResponseEntity.notFound().build();
            }
            
            Optional<Curriculum> curriculumOpt = curriculumService.getCurriculumById(curriculumId);
            if (!curriculumOpt.isPresent()) {
                response.put("message", "Curriculum not found");
                return ResponseEntity.notFound().build();
            }
            
            applicantService.createApplicantRecordFromCurriculum(applicantOpt.get(), curriculumOpt.get());
            
            response.put("message", "Curriculum records created successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating curriculum record: ", e);
            response.put("message", "Failed to create curriculum records: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{applicantId}/has-submitted")
    public ResponseEntity<Map<String, Object>> getHasSubmitted(@PathVariable Long applicantId) {
        Optional<Boolean> result = applicantService.getHasSubmitted(applicantId);
        if (result.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("applicantId", applicantId);
            response.put("hasSubmitted", result.get());
            return ResponseEntity.ok(response);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Applicant not found");
        return ResponseEntity.status(404).body(response);
    }

    @PatchMapping("/{applicantId}/has-submitted")
    public ResponseEntity<Map<String, String>> updateHasSubmitted(
            @PathVariable Long applicantId,
            @RequestParam boolean hasSubmitted) {

        Map<String, String> response = new HashMap<>();
        boolean updated = applicantService.updateHasSubmitted(applicantId, hasSubmitted);

        if (updated) {
            response.put("message", "hasSubmitted updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Applicant not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/{applicantId}/accreditation-status")
    public ResponseEntity<Map<String, Object>> getAccreditationStatus(@PathVariable Long applicantId) {
        Optional<Applicant.AccreditationStatus> result = applicantService.getAccreditationStatus(applicantId);
        if (result.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("applicantId", applicantId);
            response.put("accreditationStatus", result.get());
            return ResponseEntity.ok(response);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Applicant not found");
        return ResponseEntity.status(404).body(response);
    }

    @PatchMapping("/{applicantId}/accreditation-status")
    public ResponseEntity<Map<String, String>> updateAccreditationStatus(
            @PathVariable Long applicantId,
            @RequestParam Applicant.AccreditationStatus accreditationStatus) {

        Map<String, String> response = new HashMap<>();
        boolean updated = applicantService.updateAccreditationStatus(applicantId, accreditationStatus);

        if (updated) {
            response.put("message", "Accreditation status updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Applicant not found");
            return ResponseEntity.status(404).body(response);
        }
    }
}
