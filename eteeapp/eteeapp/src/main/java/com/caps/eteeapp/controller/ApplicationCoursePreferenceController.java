package com.caps.eteeapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caps.eteeapp.model.ApplicationCoursePreference;
import com.caps.eteeapp.service.ApplicationCoursePreferenceService;

@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<ApplicationCoursePreference>> getPreferencesByApplicationId(@PathVariable Long applicationId) {
        List<ApplicationCoursePreference> preferences = preferenceService.getPreferencesByApplicationId(applicationId);
        return ResponseEntity.ok(preferences);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }
}
