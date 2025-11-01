package com.caps.eteeapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.caps.eteeapp.dto.CoursePreferenceWithEvaluationDTO;
import com.caps.eteeapp.model.ApplicationCoursePreference;
import com.caps.eteeapp.service.ApplicationCoursePreferenceService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/preferences")
public class ApplicationCoursePreferenceController {

    @Autowired
    private ApplicationCoursePreferenceService preferenceService;

    @PostMapping
    public ResponseEntity<ApplicationCoursePreference> createPreference(@RequestBody ApplicationCoursePreference preference) {
        ApplicationCoursePreference createdPreference = preferenceService.createPreference(preference);
        return ResponseEntity.ok(createdPreference);
    }

    @PostMapping("/applicant/{applicantId}")
    public ResponseEntity<ApplicationCoursePreference> createPreference(
            @PathVariable Long applicantId,
            @RequestBody ApplicationCoursePreference preference) {
        ApplicationCoursePreference createdPreference = preferenceService.createPreferenceForApplicant(applicantId, preference);
        return ResponseEntity.ok(createdPreference);
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<ApplicationCoursePreference>> getPreferencesByApplicantId(@PathVariable Long applicantId) {
        List<ApplicationCoursePreference> preferences = preferenceService.getPreferencesByApplicantId(applicantId);
        return ResponseEntity.ok(preferences);
    }

    @GetMapping("/applicant/{applicantId}/with-evaluation")
    public ResponseEntity<List<CoursePreferenceWithEvaluationDTO>> getCoursePreferencesWithEvaluation(@PathVariable Long applicantId) {
        List<CoursePreferenceWithEvaluationDTO> dtos = preferenceService.getCoursePreferencesWithEvaluation(applicantId);
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{preferenceId}/update-status")
    public ResponseEntity<ApplicationCoursePreference> updatePreferenceStatus(
            @PathVariable Long preferenceId,
            @RequestParam ApplicationCoursePreference.Status status) {
        Optional<ApplicationCoursePreference> preference = preferenceService.getPreferenceById(preferenceId);
        if (preference.isPresent()) {
            ApplicationCoursePreference updatedPreference = preferenceService.updatePreferenceStatus(preference.get(), status);
            return ResponseEntity.ok(updatedPreference);
        }
        return ResponseEntity.status(404).body(null); // Not Found if preferenceId does not exist
    }
}
