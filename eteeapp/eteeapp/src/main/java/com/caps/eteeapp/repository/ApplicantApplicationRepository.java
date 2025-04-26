package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.ApplicantApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantApplicationRepository extends JpaRepository<ApplicantApplication, Long> {
    // Additional query methods can be added here if needed
}
