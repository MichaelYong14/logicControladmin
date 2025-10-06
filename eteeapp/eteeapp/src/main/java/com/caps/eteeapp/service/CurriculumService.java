package com.caps.eteeapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caps.eteeapp.model.Curriculum;
import com.caps.eteeapp.repository.CurriculumRepository;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository;

    public Curriculum createCurriculum(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    public List<Curriculum> getActiveCurriculums() {
        return curriculumRepository.findByIsActiveTrue();
    }

    public Optional<Curriculum> getCurriculumById(Long id) {
        return curriculumRepository.findById(id);
    }

    public Curriculum updateCurriculum(Long id, Curriculum updatedCurriculum) {
        return curriculumRepository.findById(id)
                .map(curriculum -> {
                    curriculum.setProgramName(updatedCurriculum.getProgramName());
                    curriculum.setYearStarted(updatedCurriculum.getYearStarted());
                    curriculum.setDescription(updatedCurriculum.getDescription());
                    curriculum.setIsActive(updatedCurriculum.getIsActive());
                    curriculum.setDepartment(updatedCurriculum.getDepartment());
                    return curriculumRepository.save(curriculum);
                })
                .orElseThrow(() -> new RuntimeException("Curriculum not found with id " + id));
    }

    public void deleteCurriculum(Long id) {
        curriculumRepository.deleteById(id);
    }

    public List<Curriculum> searchByProgramName(String programName) {
        return curriculumRepository.findByProgramNameContainingIgnoreCase(programName);
    }
}
