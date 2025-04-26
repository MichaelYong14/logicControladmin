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
        Applicant registeredApplicant = applicantService.registerApplicant(applicant);
        return ResponseEntity.ok(registeredApplicant);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginApplicant(@RequestParam String email, @RequestParam String password) {
        Optional<Applicant> applicant = applicantService.loginApplicant(email, password);
        if (applicant.isPresent()) {
            return ResponseEntity.ok("Login successful!");
        }
        return ResponseEntity.status(401).body("Invalid email or password.");
    }
}
    