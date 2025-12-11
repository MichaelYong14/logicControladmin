package com.caps.eteeapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caps.eteeapp.model.Assignments;
import com.caps.eteeapp.service.AssignmentsService;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentsController {

    @Autowired
    private AssignmentsService assignmentsService;

    @PostMapping
    public ResponseEntity<Assignments> createAssignment(@RequestBody AssignmentRequest request) {
        Assignments assignment = assignmentsService.createAssignment(request.getApplicantId(), request.getEvaluatorId(), request.getNotes());
        return new ResponseEntity<>(assignment, HttpStatus.CREATED);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<Assignments> getAssignment(@PathVariable Long assignmentId) {
        Assignments assignment = assignmentsService.getAssignment(assignmentId);
        return new ResponseEntity<>(assignment, HttpStatus.OK);
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<Assignments>> getAssignmentsByApplicant(@PathVariable Long applicantId) {
        List<Assignments> assignments = assignmentsService.getAssignmentsByApplicant(applicantId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    @GetMapping("/evaluator/{evaluatorId}")
    public ResponseEntity<List<Assignments>> getAssignmentsByEvaluator(@PathVariable Long evaluatorId) {
        List<Assignments> assignments = assignmentsService.getAssignmentsByEvaluator(evaluatorId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<Assignments> updateAssignment(@PathVariable Long assignmentId, @RequestBody AssignmentRequest request) {
        Assignments assignment = assignmentsService.updateAssignment(assignmentId, request.getNotes());
        return new ResponseEntity<>(assignment, HttpStatus.OK);
    }

    @PutMapping("/{assignmentId}/status")
    public ResponseEntity<Assignments> updateAssignmentStatus(@PathVariable Long assignmentId, @RequestBody StatusRequest request) {
        Assignments assignment = assignmentsService.updateAssignmentStatus(assignmentId, request.getStatus());
        return new ResponseEntity<>(assignment, HttpStatus.OK);
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long assignmentId) {
        assignmentsService.deleteAssignment(assignmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static class AssignmentRequest {
        private Long applicantId;
        private Long evaluatorId;
        private String notes;

        // Getters and setters
        public Long getApplicantId() { return applicantId; }
        public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }

        public Long getEvaluatorId() { return evaluatorId; }
        public void setEvaluatorId(Long evaluatorId) { this.evaluatorId = evaluatorId; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class StatusRequest {
        private Assignments.Status status;

        public Assignments.Status getStatus() { return status; }
        public void setStatus(Assignments.Status status) { this.status = status; }
    }
}
