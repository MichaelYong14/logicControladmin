package com.caps.eteeapp.service;

import com.caps.eteeapp.model.ApplicationCoursePreference;
import com.caps.eteeapp.repository.ApplicationCoursePreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationCoursePreferenceService {

    @Autowired
    private ApplicationCoursePreferenceRepository preferenceRepository;

    public ApplicationCoursePreference createPreference(ApplicationCoursePreference preference) {
        return preferenceRepository.save(preference);
    }

    public List<ApplicationCoursePreference> getPreferencesByApplicationId(Long applicationId) {
        return preferenceRepository.findByApplication_ApplicationId(applicationId);
    }

    public Optional<ApplicationCoursePreference> getPreferenceById(Long id) {
        return preferenceRepository.findById(id);
    }

    public void deletePreference(Long id) {
        preferenceRepository.deleteById(id);
    }
}
