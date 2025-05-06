package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByEvaluator_EvaluatorId(Long evaluatorId);
}
