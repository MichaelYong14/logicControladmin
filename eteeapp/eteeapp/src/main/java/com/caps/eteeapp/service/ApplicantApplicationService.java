package com.caps.eteeapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caps.eteeapp.model.ApplicantApplication;
import com.caps.eteeapp.model.Course;
import com.caps.eteeapp.repository.ApplicantApplicationRepository;
import com.caps.eteeapp.repository.ApplicantRepository;
import com.caps.eteeapp.repository.CourseRepository;

@Service
public class ApplicantApplicationService {

    @Autowired
    private ApplicantApplicationRepository applicationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    public ApplicantApplication createApplication(ApplicantApplication application) {
        return applicationRepository.save(application);
    }

    public List<ApplicantApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<ApplicantApplication> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public ApplicantApplication updateApplication(Long id, ApplicantApplication updatedApplication) {
        return applicationRepository.findById(id).map(application -> {
            application.setStatus(updatedApplication.getStatus());
            application.setFinalCourse(updatedApplication.getFinalCourse());
            application.setTotalCoursesSelected(updatedApplication.getTotalCoursesSelected());
            application.setApplicationNotes(updatedApplication.getApplicationNotes());
            return applicationRepository.save(application);
        }).orElseThrow(() -> new RuntimeException("Application not found with id " + id));
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    public ApplicantApplication updateApplicationStatus(Long id, String status, Long finalCourseId) {
        return applicationRepository.findById(id).map(application -> {
            application.setStatus(status);
            if (finalCourseId != null) {
                Course finalCourse = courseRepository.findById(finalCourseId)
                        .orElseThrow(() -> new RuntimeException("Course not found with id " + finalCourseId));
                application.setFinalCourse(finalCourse);
            }
            return applicationRepository.save(application);
        }).orElseThrow(() -> new RuntimeException("Application not found with id " + id));
    }

    public List<ApplicantApplication> getApplicationsByApplicantId(Long applicantId) {
        return applicationRepository.findByApplicant_ApplicantId(applicantId);
    }

    public ApplicantApplication createApplicationForApplicant(Long applicantId, ApplicantApplication application) {
        return applicantRepository.findById(applicantId).map(applicant -> {
            application.setApplicant(applicant);
            return applicationRepository.save(application);
        }).orElseThrow(() -> new RuntimeException("Applicant not found with id " + applicantId));
    }

    public void assignCourseToApplicant(Long applicantId, Long courseId) {
        // Find the applications by applicant ID
        List<ApplicantApplication> applications = applicationRepository.findByApplicant_ApplicantId(applicantId);
        
        if (applications.isEmpty()) {
            throw new RuntimeException("Application not found for applicant ID: " + applicantId);
        }
        
        // Get the first application (assuming one application per applicant)
        ApplicantApplication application = applications.get(0);
        
        // Find the course to assign
        Course finalCourse = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));
        
        // Update the final course assignment
        application.setFinalCourse(finalCourse);
        application.setStatus("ASSIGNED");
        
        // Save the updated application
        applicationRepository.save(application);
    }
}
