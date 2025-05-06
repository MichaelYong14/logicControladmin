package com.caps.eteeapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ApplicationCoursePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceId;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicantApplication application;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityOrder priorityOrder;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum PriorityOrder {
        FIRST, SECOND, THIRD
    }

    public enum Status {
        PENDING, REVIEWED, ACCEPTED, REJECTED
    }

    // Getters and setters...
}
