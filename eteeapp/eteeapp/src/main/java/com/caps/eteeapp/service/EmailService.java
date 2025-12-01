package com.caps.eteeapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // Optional — app runs without a mail sender configured.
    @Autowired(required = false)
    private JavaMailSender mailSender;

    /**
     * Attempts to send an email. Returns EmailSendResult with success and optional error message.
     * Detailed events/errors are logged for debugging.
     */
    public EmailSendResult sendEmail(String to, String subject, String text) {
        if (to == null || to.trim().isEmpty()) {
            logger.debug("Email send aborted: recipient is null/empty");
            return new EmailSendResult(false, "recipient-empty");
        }

        String normalized = to.trim().toLowerCase();
        logger.debug("EmailService: preparing to send to '{}'", normalized);

        if (mailSender == null) {
            logger.warn("EmailService: no JavaMailSender configured; cannot send email to {}", normalized);
            return new EmailSendResult(false, "no-mail-sender-configured");
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            logger.debug("EmailService: sending message to {}", normalized);
            mailSender.send(msg);
            logger.info("EmailService: message sent successfully to {}", normalized);
            return new EmailSendResult(true, null);
        } catch (Exception ex) {
            String err = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            logger.error("EmailService: failed to send email to {} — {}", normalized, err, ex);
            return new EmailSendResult(false, err);
        }
    }

    // Simple result holder to communicate status + error to callers.
    public static class EmailSendResult {
        private final boolean sent;
        private final String error;

        public EmailSendResult(boolean sent, String error) {
            this.sent = sent;
            this.error = error;
        }

        public boolean isSent() {
            return sent;
        }

        public String getError() {
            return error;
        }
    }
}
