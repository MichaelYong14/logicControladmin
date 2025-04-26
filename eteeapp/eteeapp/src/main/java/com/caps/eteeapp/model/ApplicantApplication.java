package com.caps.eteeapp.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;

@Data
@Entity
public class ApplicantApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @OneToOne
    @JoinColumn(name = "applicant_id", unique = true)
    private Applicant applicant;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSubmitted;

    private String status;

    @ManyToOne
    @JoinColumn(name = "final_course_id")
    private Course finalCourse;

    private int totalCoursesSelected;

    @Lob
    private String applicationNotes;

    // Getters and setters...
}
