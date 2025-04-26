package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.ProgramAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramAdminRepository extends JpaRepository<ProgramAdmin, Long> {
    // Additional query methods can be added here if needed
}
