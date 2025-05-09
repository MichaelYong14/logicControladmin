package com.caps.eteeapp.controller;

import com.caps.eteeapp.model.ApplicantApplication;
import com.caps.eteeapp.model.ApplicationCoursePreference;
import com.caps.eteeapp.service.ApplicantApplicationService;
import com.caps.eteeapp.service.ApplicationCoursePreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/program-admins")
public class ProgramAdminController {

    @Autowired
    private ApplicantApplicationService applicationService;

    @Autowired
    private ApplicationCoursePreferenceService preferenceService;

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicantApplication>> getAllApplications() {
        List<ApplicantApplication> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicantApplication> getApplicationById(@PathVariable Long id) {
        return applicationService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/applications/{id}/preferences")
    public ResponseEntity<List<ApplicationCoursePreference>> getCoursePreferencesByApplicantId(@PathVariable Long id) {
        List<ApplicationCoursePreference> preferences = preferenceService.getPreferencesByApplicantId(id);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/applications/{id}/update-status")
    public ResponseEntity<ApplicantApplication> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Long finalCourseId) {
        try {
            ApplicantApplication updatedApplication = applicationService.updateApplicationStatus(id, status, finalCourseId);
            return ResponseEntity.ok(updatedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
