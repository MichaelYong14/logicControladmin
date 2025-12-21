package com.caps.eteeapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caps.eteeapp.model.ApplicantApplication;
import com.caps.eteeapp.model.Course;
import com.caps.eteeapp.model.Notification;
// import com.caps.eteeapp.service.NotificationService;
import com.caps.eteeapp.repository.ApplicantApplicationRepository;
import com.caps.eteeapp.repository.ApplicantRepository;
import com.caps.eteeapp.repository.CourseRepository;

@Service
public class ApplicantApplicationService {

    @Autowired
    private ApplicantApplicationRepository applicationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private NotificationService notificationService;

    public ApplicantApplication createApplication(ApplicantApplication application) {
        return applicationRepository.save(application);
    }

    public List<ApplicantApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<ApplicantApplication> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public ApplicantApplication updateApplication(Long id, ApplicantApplication updatedApplication) {
        return applicationRepository.findById(id).map(application -> {
            String oldStatus = application.getStatus();
            String newStatus = updatedApplication.getStatus();

            application.setStatus(newStatus);
            application.setFinalCourse(updatedApplication.getFinalCourse());
            application.setTotalCoursesSelected(updatedApplication.getTotalCoursesSelected());
            application.setApplicationNotes(updatedApplication.getApplicationNotes());

            ApplicantApplication saved = applicationRepository.save(application);

            // If status changed, create a notification for the applicant
            try {
                if (oldStatus != null && newStatus != null && !oldStatus.equals(newStatus)) {
                    Long applicantId = application.getApplicant() != null ? application.getApplicant().getApplicantId() : null;
                    System.out.println("DEBUG: applicantId=" + applicantId);
                    System.out.println("DEBUG: applicant=" + application.getApplicant());
                    if (applicantId != null && notificationService != null) {
                        // Custom notification for APPROVED status
                        if ("APPROVED".equalsIgnoreCase(newStatus)) {
                            String firstName = application.getApplicant() != null ? application.getApplicant().getFirstName() : "";
                            System.out.println("DEBUG: Sending APPROVED notification to " + firstName);
                            String title = "Application Status Update";
                            String message = String.format(
                                "Hi %s, your application to the ETEEAP Program has been approved by Chair. This application will now be forwarded to the department of the courses you've chosen.",
                                firstName
                            );
                            Notification.NotificationType type = Notification.NotificationType.SUCCESS;
                            Notification notif = notificationService.createNotification(
                                applicantId, null, null, title, message, type, null
                            );
                            System.out.println("DEBUG: Notification created: " + notif);
                        } else {
                            String title = "Application Status Updated";
                            String message = String.format("Your application status changed from %s to %s.", oldStatus, newStatus);
                            Notification.NotificationType type = Notification.NotificationType.INFO;
                            if ("ACCEPTED".equalsIgnoreCase(newStatus)) type = Notification.NotificationType.SUCCESS;
                            else if ("REJECTED".equalsIgnoreCase(newStatus)) type = Notification.NotificationType.ERROR;
                            else if ("PENDING".equalsIgnoreCase(newStatus) || "UNDER_REVIEW".equalsIgnoreCase(newStatus)) type = Notification.NotificationType.WARNING;

                            Notification notif = notificationService.createNotification(
                                applicantId, null, null, title, message, type, null
                            );
                            System.out.println("DEBUG: Notification created: " + notif);
                        }
                    } else {
                        System.out.println("DEBUG: applicantId or notificationService is null, notification not sent.");
                    }
                }
            } catch (Exception ex) {
                // Do not block the update if notification fails
                System.err.println("Failed to create application status notification: " + ex.getMessage());
            }

            return saved;
        }).orElseThrow(() -> new RuntimeException("Application not found with id " + id));
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    /**
     * Existing three-arg method now delegates to the four-arg variant for compatibility.
     */
    public ApplicantApplication updateApplicationStatus(Long id, String status, Long finalCourseId) {
        return updateApplicationStatus(id, status, finalCourseId, null);
    }

    /**
     * Update application status and optionally set finalCourseId and applicationNotes, then persist.
     * Also sends notification to applicant if status changes.
     */
    public ApplicantApplication updateApplicationStatus(Long id, String status, Long finalCourseId, String applicationNotes) {
        Optional<ApplicantApplication> opt = applicationRepository.findById(id);
        if (opt.isPresent()) {
            ApplicantApplication application = opt.get();

            String oldStatus = application.getStatus();
            String newStatus = status;

            // update status
            application.setStatus(newStatus);

            // set final course reference by id if provided
            if (finalCourseId != null) {
                Course c = new Course();
                c.setCourseId(finalCourseId);
                application.setFinalCourse(c);
            }

            // persist applicationNotes if provided (overwrite existing notes)
            if (applicationNotes != null) {
                application.setApplicationNotes(applicationNotes);
            }

            ApplicantApplication saved = applicationRepository.save(application);

            // If status changed, create a notification for the applicant
            try {
                if (oldStatus != null && newStatus != null && !oldStatus.equals(newStatus)) {
                    Long applicantId = application.getApplicant() != null ? application.getApplicant().getApplicantId() : null;
                    System.out.println("DEBUG: applicantId=" + applicantId);
                    System.out.println("DEBUG: applicant=" + application.getApplicant());
                    if (applicantId != null && notificationService != null) {
                        // Custom notification for APPROVED status
                        if ("APPROVED".equalsIgnoreCase(newStatus)) {
                            String firstName = application.getApplicant() != null ? application.getApplicant().getFirstName() : "";
                            System.out.println("DEBUG: Sending APPROVED notification to " + firstName);
                            String title = "Application Status Update";
                            String message = String.format(
                                "Hi %s, your application to the ETEEAP Program has been approved by Chair. This application will now be forwarded to the department of the courses you've chosen.",
                                firstName
                            );
                            Notification.NotificationType type = Notification.NotificationType.SUCCESS;
                            Notification notif = notificationService.createNotification(
                                applicantId, null, null, title, message, type, null
                            );
                            System.out.println("DEBUG: Notification created: " + notif);
                        } else {
                            String title = "Application Status Updated";
                            String message = String.format("Your application status changed from %s to %s.", oldStatus, newStatus);
                            Notification.NotificationType type = Notification.NotificationType.INFO;
                            if ("ACCEPTED".equalsIgnoreCase(newStatus)) type = Notification.NotificationType.SUCCESS;
                            else if ("REJECTED".equalsIgnoreCase(newStatus)) type = Notification.NotificationType.ERROR;
                            else if ("PENDING".equalsIgnoreCase(newStatus) || "UNDER_REVIEW".equalsIgnoreCase(newStatus)) type = Notification.NotificationType.WARNING;

                            Notification notif = notificationService.createNotification(
                                applicantId, null, null, title, message, type, null
                            );
                            System.out.println("DEBUG: Notification created: " + notif);
                        }
                    } else {
                        System.out.println("DEBUG: applicantId or notificationService is null, notification not sent.");
                    }
                }
            } catch (Exception ex) {
                // Do not block the update if notification fails
                System.err.println("Failed to create application status notification: " + ex.getMessage());
            }

            return saved;
        } else {
            throw new RuntimeException("Application not found with id " + id);
        }
    }

    public List<ApplicantApplication> getApplicationsByApplicantId(Long applicantId) {
        return applicationRepository.findByApplicant_ApplicantId(applicantId);
    }

    public ApplicantApplication createApplicationForApplicant(Long applicantId, ApplicantApplication application) {
        return applicantRepository.findById(applicantId).map(applicant -> {
            application.setApplicant(applicant);
            return applicationRepository.save(application);
        }).orElseThrow(() -> new RuntimeException("Applicant not found with id " + applicantId));
    }

    public void assignCourseToApplicant(Long applicantId, Long courseId) {
        // Find the applications by applicant ID
        List<ApplicantApplication> applications = applicationRepository.findByApplicant_ApplicantId(applicantId);
        
        if (applications.isEmpty()) {
            throw new RuntimeException("Application not found for applicant ID: " + applicantId);
        }
        
        // Get the first application (assuming one application per applicant)
        ApplicantApplication application = applications.get(0);
        
        // Find the course to assign
        Course finalCourse = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));
        
        // Update the final course assignment
        application.setFinalCourse(finalCourse);
        application.setStatus("ASSIGNED");
        
        // Save the updated application
        applicationRepository.save(application);
    }
}
