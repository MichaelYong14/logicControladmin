package com.caps.eteeapp.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.model.Assignments;
import com.caps.eteeapp.model.Evaluator;
import com.caps.eteeapp.repository.AssignmentsRepository;
import com.caps.eteeapp.repository.ApplicantRepository;
import com.caps.eteeapp.repository.EvaluatorRepository;

@Service
public class AssignmentsService {

    @Autowired
    private AssignmentsRepository assignmentsRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private EvaluatorRepository evaluatorRepository;

    public Assignments createAssignment(Long applicantId, Long evaluatorId, String notes) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        Evaluator evaluator = evaluatorRepository.findById(evaluatorId)
                .orElseThrow(() -> new RuntimeException("Evaluator not found"));

        Assignments assignment = new Assignments();
        assignment.setApplicant(applicant);
        assignment.setEvaluator(evaluator);
        assignment.setAssignedDate(new Date());
        assignment.setNotes(notes);
        assignment.setStatus(Assignments.Status.PENDING);

        return assignmentsRepository.save(assignment);
    }

    public List<Assignments> getAssignmentsByApplicant(Long applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        return assignmentsRepository.findByApplicant(applicant);
    }

    public List<Assignments> getAssignmentsByEvaluator(Long evaluatorId) {
        Evaluator evaluator = evaluatorRepository.findById(evaluatorId)
                .orElseThrow(() -> new RuntimeException("Evaluator not found"));
        return assignmentsRepository.findByEvaluator(evaluator);
    }

    public Assignments getAssignment(Long assignmentId) {
        return assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    public Assignments updateAssignment(Long assignmentId, String notes) {
        Assignments assignment = assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setNotes(notes);
        return assignmentsRepository.save(assignment);
    }

    public Assignments updateAssignmentStatus(Long assignmentId, Assignments.Status status) {
        Assignments assignment = assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setStatus(status);
        return assignmentsRepository.save(assignment);
    }

    public void deleteAssignment(Long assignmentId) {
        assignmentsRepository.deleteById(assignmentId);
    }
}
