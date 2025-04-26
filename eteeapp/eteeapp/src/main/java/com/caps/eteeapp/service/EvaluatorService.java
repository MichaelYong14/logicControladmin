package com.caps.eteeapp.service;

import com.caps.eteeapp.model.Evaluator;
import com.caps.eteeapp.repository.EvaluatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EvaluatorService {

    @Autowired
    private EvaluatorRepository evaluatorRepository;

    public Evaluator registerEvaluator(Evaluator evaluator) {
        // Save the evaluator with plaintext password (for testing purposes)
        return evaluatorRepository.save(evaluator);
    }

    public Optional<Evaluator> loginEvaluator(String email, String password) {
        Optional<Evaluator> evaluator = evaluatorRepository.findByEmail(email);
        if (evaluator.isPresent() && password.equals(evaluator.get().getPassword())) {
            return evaluator;
        }
        return Optional.empty();
    }
}
