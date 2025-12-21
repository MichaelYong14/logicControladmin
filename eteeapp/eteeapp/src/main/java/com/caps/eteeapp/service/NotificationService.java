package com.caps.eteeapp.service;

import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.model.Evaluator;
import com.caps.eteeapp.model.ProgramAdmin;
import com.caps.eteeapp.model.Notification;
import com.caps.eteeapp.repository.ApplicantRepository;
import com.caps.eteeapp.repository.EvaluatorRepository;
import com.caps.eteeapp.repository.ProgramAdminRepository;
import com.caps.eteeapp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private EvaluatorRepository evaluatorRepository;

    @Autowired
    private ProgramAdminRepository programAdminRepository;

    // Create notification for any user type
    public Notification createNotification(Long applicantId, Long evaluatorId, Long programAdminId, String title, String message, Notification.NotificationType type, String clientTempId) {
        Notification notification = new Notification();
        if (applicantId != null) {
            Applicant applicant = applicantRepository.findById(applicantId).orElse(null);
            if (applicant == null) return null;
            notification.setApplicant(applicant);
        }
        if (evaluatorId != null) {
            Evaluator evaluator = evaluatorRepository.findById(evaluatorId).orElse(null);
            if (evaluator == null) return null;
            notification.setEvaluator(evaluator);
        }
        if (programAdminId != null) {
            ProgramAdmin programAdmin = programAdminRepository.findById(programAdminId).orElse(null);
            if (programAdmin == null) return null;
            notification.setProgramAdmin(programAdmin);
        }
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setClientTempId(clientTempId);
        notification.setType(type != null ? type : Notification.NotificationType.INFO);
        notification.setCreatedAt(new Date());
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    // Fetch for Applicant
    public List<Notification> getNotificationsForApplicant(Long applicantId) {
        return notificationRepository.findByApplicant_ApplicantIdOrderByCreatedAtDesc(applicantId);
    }

    public List<Notification> getUnreadNotificationsForApplicant(Long applicantId) {
        return notificationRepository.findByApplicant_ApplicantIdAndReadFalseOrderByCreatedAtDesc(applicantId);
    }

    // Fetch for Evaluator
    public List<Notification> getNotificationsForEvaluator(Long evaluatorId) {
        return notificationRepository.findByEvaluator_EvaluatorIdOrderByCreatedAtDesc(evaluatorId);
    }

    public List<Notification> getUnreadNotificationsForEvaluator(Long evaluatorId) {
        return notificationRepository.findByEvaluator_EvaluatorIdAndReadFalseOrderByCreatedAtDesc(evaluatorId);
    }

    // Fetch for ProgramAdmin
    public List<Notification> getNotificationsForProgramAdmin(Long programAdminId) {
        return notificationRepository.findByProgramAdmin_AdminIdOrderByCreatedAtDesc(programAdminId);
    }

    public List<Notification> getUnreadNotificationsForProgramAdmin(Long programAdminId) {
        return notificationRepository.findByProgramAdmin_AdminIdAndReadFalseOrderByCreatedAtDesc(programAdminId);
    }

    public boolean markAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId).map(n -> {
            n.setRead(true);
            notificationRepository.save(n);
            return true;
        }).orElse(false);
    }

    public boolean markAllAsReadForApplicant(Long applicantId) {
        List<Notification> list = notificationRepository.findByApplicant_ApplicantIdOrderByCreatedAtDesc(applicantId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
        return true;
    }

    public boolean markAllAsReadForEvaluator(Long evaluatorId) {
        List<Notification> list = notificationRepository.findByEvaluator_EvaluatorIdOrderByCreatedAtDesc(evaluatorId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
        return true;
    }

    public boolean markAllAsReadForProgramAdmin(Long programAdminId) {
        List<Notification> list = notificationRepository.findByProgramAdmin_AdminIdOrderByCreatedAtDesc(programAdminId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
        return true;
    }
}
// All repository usage is correct for Evaluator and ProgramAdmin in notification creation and retrieval.
