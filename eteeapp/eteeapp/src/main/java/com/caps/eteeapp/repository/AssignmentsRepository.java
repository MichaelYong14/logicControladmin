package com.caps.eteeapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.model.Assignments;
import com.caps.eteeapp.model.Evaluator;

@Repository
public interface AssignmentsRepository extends JpaRepository<Assignments, Long> {
    List<Assignments> findByApplicant(Applicant applicant);
    List<Assignments> findByEvaluator(Evaluator evaluator);
    Assignments findByApplicantAndEvaluator(Applicant applicant, Evaluator evaluator);
}
