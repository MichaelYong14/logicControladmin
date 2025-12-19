package com.caps.eteeapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caps.eteeapp.model.*;
import com.caps.eteeapp.repository.EvaluationNotificationRepository;

@Service
public class EvaluationNotificationsService {

    @Autowired
    private EvaluationNotificationRepository notificationRepository;

    public EvaluationNotification createForwardedNotification(
            ApplicantApplication application,
            Course course,
            Evaluator evaluator
    ) {
        EvaluationNotification notification = new EvaluationNotification();
        notification.setApplication(application);
        notification.setCourse(course);
        notification.setEvaluator(evaluator);
        notification.setRead(false);

        String applicantName = "Applicant";
        if (application != null && application.getApplicant() != null) {
            Applicant applicant = application.getApplicant();
            StringBuilder sb = new StringBuilder();
            if (applicant.getFirstName() != null) sb.append(applicant.getFirstName()).append(" ");
            if (applicant.getMiddleInitial() != null) sb.append(applicant.getMiddleInitial()).append(" ");
            if (applicant.getLastName() != null) sb.append(applicant.getLastName());
            applicantName = sb.toString().trim();
        }
        

        String message = String.format(
            "%s's application has been forwarded by ETEEAP Coordinator.",
            applicantName
        );
        notification.setMessage(message);

        return notificationRepository.save(notification);
    }

    public List<EvaluationNotification> getNotificationsByEvaluatorId(Long evaluatorId) {
        return notificationRepository.findByEvaluator_EvaluatorId(evaluatorId);
    }

    @Transactional
    public boolean markNotificationAsRead(Long notificationId) {
        return notificationRepository.markAsRead(notificationId) > 0;
    }
}
