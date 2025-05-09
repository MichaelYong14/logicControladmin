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

    public Applicant registerApplicant(String email, String password) {
        Applicant applicant = new Applicant();
        applicant.setEmail(email);
        applicant.setPassword(password);
        return applicantRepository.save(applicant);
    }

    public Optional<Applicant> loginApplicant(String email, String password) {
        Optional<Applicant> applicant = applicantRepository.findByEmail(email);
        if (applicant.isPresent() && password.equals(applicant.get().getPassword())) {
            return applicant;
        }
        return Optional.empty();
    }

    public Optional<Applicant> findApplicantById(Long applicantId) {
        return applicantRepository.findById(applicantId);
    }

    public Applicant updateApplicant(Applicant existingApplicant, Applicant updatedApplicant) {
        if (updatedApplicant.getFirstName() != null) {
            existingApplicant.setFirstName(updatedApplicant.getFirstName());
        }
        if (updatedApplicant.getMiddleInitial() != null) {
            existingApplicant.setMiddleInitial(updatedApplicant.getMiddleInitial());
        }
        if (updatedApplicant.getLastName() != null) {
            existingApplicant.setLastName(updatedApplicant.getLastName());
        }
        if (updatedApplicant.getContactNumber() != null) {
            existingApplicant.setContactNumber(updatedApplicant.getContactNumber());
        }
        if (updatedApplicant.getAddress() != null) {
            existingApplicant.setAddress(updatedApplicant.getAddress());
        }
        if (updatedApplicant.getProfileDetails() != null) {
            existingApplicant.setProfileDetails(updatedApplicant.getProfileDetails());
        }
        if (updatedApplicant.getDateOfBirth() != null) {
            existingApplicant.setDateOfBirth(updatedApplicant.getDateOfBirth());
        }
        if (updatedApplicant.getGender() != null) {
            existingApplicant.setGender(updatedApplicant.getGender());
        }
        return applicantRepository.save(existingApplicant);
    }
}
