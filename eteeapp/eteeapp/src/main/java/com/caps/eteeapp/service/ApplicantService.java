package com.caps.eteeapp.service;

import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.repository.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicantService {

    @Autowired
    private ApplicantRepository applicantRepository;

    public Applicant registerApplicant(Applicant applicant) {
        // Save the applicant with plaintext password (for testing purposes)
        return applicantRepository.save(applicant);
    }

    public Optional<Applicant> loginApplicant(String email, String password) {
        Optional<Applicant> applicant = applicantRepository.findByEmail(email);
        if (applicant.isPresent() && password.equals(applicant.get().getPassword())) {
            return applicant;
        }
        return Optional.empty();
    }
}
