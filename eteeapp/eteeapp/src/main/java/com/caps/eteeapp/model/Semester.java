package com.caps.eteeapp.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer yearLevel;

    @Column(nullable = false)
    private Integer semesterNumber;

    @ManyToOne
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subject> subjects;

    @Column(length = 500)
    private String description;
}
