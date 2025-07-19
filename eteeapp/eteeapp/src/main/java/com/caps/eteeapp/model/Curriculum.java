package com.caps.eteeapp.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String programName;

    @Column(nullable = false)
    private Integer yearStarted;

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Semester> semesters;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;
}
