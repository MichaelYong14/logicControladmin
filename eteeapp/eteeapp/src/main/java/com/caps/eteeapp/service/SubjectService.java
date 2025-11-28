package com.caps.eteeapp.service;

import com.caps.eteeapp.model.Subject;
import com.caps.eteeapp.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<Subject> getSubjectsBySemester(Long semesterId) {
        return subjectRepository.findBySemester_Id(semesterId);
    }

    public Subject updateSubject(Long id, Subject updatedSubject) {
        return subjectRepository.findById(id).map(subject -> {
            subject.setSubjectCode(updatedSubject.getSubjectCode());
            subject.setDescriptiveTitle(updatedSubject.getDescriptiveTitle());
            subject.setLecHours(updatedSubject.getLecHours());
            subject.setLabHours(updatedSubject.getLabHours());
            subject.setUnits(updatedSubject.getUnits());
            subject.setSemester(updatedSubject.getSemester());
            subject.setDescription(updatedSubject.getDescription());
            subject.setPrerequisites(updatedSubject.getPrerequisites());
            return subjectRepository.save(subject);
        }).orElseThrow(() -> new RuntimeException("Subject not found with id " + id));
    }
}
