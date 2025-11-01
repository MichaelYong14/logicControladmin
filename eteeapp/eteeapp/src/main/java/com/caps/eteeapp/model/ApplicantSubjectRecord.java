package com.caps.eteeapp.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
public class ApplicantSubjectRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    @JsonBackReference
    private Applicant applicant;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(length = 10)
    private String grade;

    @Column(length = 500)
    private String processOfAccreditation;

    @Column(length = 1000)
    private String substantiveBasis;

    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate = new Date();

    @Enumerated(EnumType.STRING)
    private RecordStatus status = RecordStatus.PENDING;

    public enum RecordStatus {
        PENDING, APPROVED, REJECTED
    }
}
