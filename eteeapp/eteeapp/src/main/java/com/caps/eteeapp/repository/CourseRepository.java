package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Additional query methods can be added here if needed
}
