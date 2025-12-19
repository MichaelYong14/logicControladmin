package com.caps.eteeapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;
import com.caps.eteeapp.model.EvaluationNotification;

public interface EvaluationNotificationRepository extends JpaRepository<EvaluationNotification, Long> {
    // Add custom queries if needed
    List<EvaluationNotification> findByEvaluator_EvaluatorId(Long evaluatorId);

    @Transactional
    @Modifying
    @Query("UPDATE EvaluationNotification n SET n.read = true WHERE n.notificationId = :notificationId")
    int markAsRead(Long notificationId);
}
