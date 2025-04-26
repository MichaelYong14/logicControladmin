package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    Optional<Applicant> findByEmail(String email);
}
