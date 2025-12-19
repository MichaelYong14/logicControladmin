package com.caps.eteeapp.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class EvaluationNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "evaluator_id", nullable = false)
    private Evaluator evaluator;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicantApplication application;

    @Column(nullable = false)
    private Boolean read = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateCreated = new Date();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
}
