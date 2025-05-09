package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.ApplicationCoursePreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationCoursePreferenceRepository extends JpaRepository<ApplicationCoursePreference, Long> {
    List<ApplicationCoursePreference> findByApplicant_ApplicantId(Long applicantId);
}
