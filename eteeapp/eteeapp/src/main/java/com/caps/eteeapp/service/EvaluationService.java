package com.caps.eteeapp.service;

import com.caps.eteeapp.model.*;
import com.caps.eteeapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {

    @Autowired
    private ApplicantApplicationRepository applicationRepository;

    @Autowired
    private ApplicationCoursePreferenceRepository preferenceRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    // @Autowired
    // private CourseRepository courseRepository;

    // @Autowired
    // private DepartmentRepository departmentRepository;

    public List<Evaluation> forwardApplication(Long applicationId) {
        ApplicantApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id " + applicationId));

        List<ApplicationCoursePreference> preferences = preferenceRepository.findByApplication_ApplicationId(applicationId);

        List<Evaluation> evaluations = new ArrayList<>();
        for (ApplicationCoursePreference preference : preferences) {
            Evaluation evaluation = new Evaluation();
            evaluation.setApplication(application);
            evaluation.setCourse(preference.getCourse());
            evaluation.setEvaluationStatus(Evaluation.EvaluationStatus.PENDING);
            evaluation.setDateEvaluated(new Date());
            evaluations.add(evaluationRepository.save(evaluation));
        }

        return evaluations;
    }

    public List<Evaluation> forwardToDepartment(Long applicationId) {
        ApplicantApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id " + applicationId));

        List<ApplicationCoursePreference> preferences = preferenceRepository.findByApplication_ApplicationId(applicationId);

        List<Evaluation> evaluations = new ArrayList<>();
        for (ApplicationCoursePreference preference : preferences) {
            Course course = preference.getCourse();
            Department department = course.getDepartment();

            if (department != null && department.getDepartmentHead() != null) {
                Evaluator evaluator = department.getDepartmentHead();

                Evaluation evaluation = new Evaluation();
                evaluation.setApplication(application);
                evaluation.setCourse(course);
                evaluation.setEvaluator(evaluator);
                evaluation.setEvaluationStatus(Evaluation.EvaluationStatus.PENDING);
                evaluation.setDateEvaluated(new Date());
                evaluations.add(evaluationRepository.save(evaluation));
            }
        }

        return evaluations;
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public List<Evaluation> getEvaluationsByEvaluatorId(Long evaluatorId) {
        return evaluationRepository.findByEvaluator_EvaluatorId(evaluatorId);
    }

    public Optional<Evaluation> findEvaluationById(Long evaluationId) {
        return evaluationRepository.findById(evaluationId);
    }

    public Evaluation updateEvaluationStatus(Evaluation evaluation, Evaluation.EvaluationStatus status) {
        evaluation.setEvaluationStatus(status);
        return evaluationRepository.save(evaluation);
    }
}
