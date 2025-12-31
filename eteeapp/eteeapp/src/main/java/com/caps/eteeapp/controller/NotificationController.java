package com.caps.eteeapp.controller;

import com.caps.eteeapp.model.Notification;
import com.caps.eteeapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Applicant endpoints
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long applicantId) {
        List<Notification> list = notificationService.getNotificationsForApplicant(applicantId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/applicant/{applicantId}/unread")
    public ResponseEntity<List<Notification>> getUnread(@PathVariable Long applicantId) {
        List<Notification> list = notificationService.getUnreadNotificationsForApplicant(applicantId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/applicant/{applicantId}/read-all")
    public ResponseEntity<?> markAllRead(@PathVariable Long applicantId) {
        notificationService.markAllAsReadForApplicant(applicantId);
        return ResponseEntity.ok().build();
    }

    // Evaluator endpoints
    @GetMapping("/evaluator/{evaluatorId}")
    public ResponseEntity<List<Notification>> getNotificationsForEvaluator(@PathVariable Long evaluatorId) {
        List<Notification> list = notificationService.getNotificationsForEvaluator(evaluatorId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/evaluator/{evaluatorId}/unread")
    public ResponseEntity<List<Notification>> getUnreadForEvaluator(@PathVariable Long evaluatorId) {
        List<Notification> list = notificationService.getUnreadNotificationsForEvaluator(evaluatorId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/evaluator/{evaluatorId}/read-all")
    public ResponseEntity<?> markAllReadForEvaluator(@PathVariable Long evaluatorId) {
        notificationService.markAllAsReadForEvaluator(evaluatorId);
        return ResponseEntity.ok().build();
    }

    // ProgramAdmin endpoints
    @GetMapping("/program-admin/{programAdminId}")
    public ResponseEntity<List<Notification>> getNotificationsForProgramAdmin(@PathVariable Long programAdminId) {
        List<Notification> list = notificationService.getNotificationsForProgramAdmin(programAdminId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/program-admin/{programAdminId}/unread")
    public ResponseEntity<List<Notification>> getUnreadForProgramAdmin(@PathVariable Long programAdminId) {
        List<Notification> list = notificationService.getUnreadNotificationsForProgramAdmin(programAdminId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/program-admin/{programAdminId}/read-all")
    public ResponseEntity<?> markAllReadForProgramAdmin(@PathVariable Long programAdminId) {
        notificationService.markAllAsReadForProgramAdmin(programAdminId);
        return ResponseEntity.ok().build();
    }

    // Create notification for any user type
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification payload) {
        if (payload == null) return ResponseEntity.badRequest().build();
        Long applicantId = payload.getApplicant() != null ? payload.getApplicant().getApplicantId() : null;
        Long evaluatorId = payload.getEvaluator() != null ? payload.getEvaluator().getEvaluatorId() : null;
        Long programAdminId = payload.getProgramAdmin() != null ? payload.getProgramAdmin().getProgramAdminId() : null;
        if (applicantId == null && evaluatorId == null && programAdminId == null) return ResponseEntity.badRequest().build();
        Notification.NotificationType type = payload.getType();
        String clientTempId = payload.getClientTempId();
        String category = payload.getCategory();
        String action = payload.getAction();
        Notification created = notificationService.createNotification(
                applicantId, evaluatorId, programAdminId, payload.getTitle(), payload.getMessage(), type, clientTempId, category, action);
        if (created == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        boolean ok = notificationService.markAsRead(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Controller correctly delegates to NotificationService for all user types.
}
