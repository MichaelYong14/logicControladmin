package com.caps.eteeapp.repository;

import com.caps.eteeapp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByApplicant_ApplicantIdOrderByCreatedAtDesc(Long applicantId);

    List<Notification> findByApplicant_ApplicantIdAndReadFalseOrderByCreatedAtDesc(Long applicantId);

    // For Evaluator
    List<Notification> findByEvaluator_EvaluatorIdOrderByCreatedAtDesc(Long evaluatorId);
    List<Notification> findByEvaluator_EvaluatorIdAndReadFalseOrderByCreatedAtDesc(Long evaluatorId);

    // For ProgramAdmin
    List<Notification> findByProgramAdmin_AdminIdOrderByCreatedAtDesc(Long adminId);
    List<Notification> findByProgramAdmin_AdminIdAndReadFalseOrderByCreatedAtDesc(Long adminId);
}
