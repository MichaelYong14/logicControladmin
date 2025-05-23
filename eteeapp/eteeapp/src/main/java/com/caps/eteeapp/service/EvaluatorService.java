package com.caps.eteeapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caps.eteeapp.model.Department;
import com.caps.eteeapp.model.Evaluation;
import com.caps.eteeapp.model.Evaluator;
import com.caps.eteeapp.repository.EvaluatorRepository;

@Service
public class EvaluatorService {

    @Autowired
    private EvaluatorRepository evaluatorRepository;

    public List<Evaluator> getAllEvaluators() {
        return evaluatorRepository.findAll();
    }

    public Evaluator registerEvaluator(Evaluator evaluator) {
        // Save the evaluator without modifying the isAdmin field
        return evaluatorRepository.save(evaluator);
    }

    public Optional<Evaluator> loginEvaluator(String email, String password) {
        Optional<Evaluator> evaluator = evaluatorRepository.findByEmail(email);
        if (evaluator.isPresent() && password.equals(evaluator.get().getPassword())) {
            return evaluator;
        }
        return Optional.empty();
    }

    public Optional<Evaluator> findEvaluatorById(Long evaluatorId) {
        return evaluatorRepository.findById(evaluatorId);
    }
    
    public Optional<Evaluator> findByEmail(String email) {
        return evaluatorRepository.findByEmail(email);
    }

    public Evaluator updateAdminStatus(Evaluator evaluator, boolean isAdmin) {
        evaluator.setAdmin(isAdmin);
        return evaluatorRepository.save(evaluator);
    }

    public List<Evaluation> getEvaluationsByDepartment(Department department) {
        // This method filters evaluations where the course department 
        // matches the given department
        return evaluatorRepository.findEvaluationsByCourseDepartment(department);
    }
}