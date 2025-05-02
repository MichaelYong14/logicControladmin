package com.caps.eteeapp.controller;

import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/applicants")
public class ApplicantController {

    @Autowired
    private ApplicantService applicantService;

    @PostMapping("/register")
    public ResponseEntity<Applicant> registerApplicant(@RequestBody Applicant applicant) {
        Applicant registeredApplicant = applicantService.registerApplicant(applicant.getEmail(), applicant.getPassword());
        return ResponseEntity.ok(registeredApplicant);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginApplicant(@RequestBody Applicant loginRequest) {
        Optional<Applicant> applicant = applicantService.loginApplicant(loginRequest.getEmail(), loginRequest.getPassword());
        if (applicant.isPresent()) {
            Applicant loggedInApplicant = applicant.get();
            if (loggedInApplicant.getFirstName() == null || loggedInApplicant.getLastName() == null) {
                return ResponseEntity.ok("Login successful! Please complete your profile.");
            }
            return ResponseEntity.ok("Login successful!");
        }
        return ResponseEntity.status(401).body("Invalid email or password.");
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
}
