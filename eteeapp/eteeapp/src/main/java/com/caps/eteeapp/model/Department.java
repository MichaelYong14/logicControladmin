    package com.caps.eteeapp.model;

    import jakarta.persistence.*;
    import lombok.Data;
    import com.fasterxml.jackson.annotation.JsonIgnore;

    import java.util.List;

    @Data
    @Entity
    public class Department {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long departmentId;

        private String departmentName;

        @ManyToOne
        @JoinColumn(name = "department_head_id")
        @JsonIgnore
        private Evaluator departmentHead;

        @OneToMany(mappedBy = "department")
        @JsonIgnore  // This prevents infinite recursion
        private List<Course> courses;

        @Lob
        private String contactInfo;

        // Getters and setters...
    }
