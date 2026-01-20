package de.othr.event_hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
import jakarta.mail.internet.MimeMessage;

// https://www.youtube.com/watch?v=kLMUS0-PznE (Email Service Configuration)

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private TemplateEngine templateEngine;

    public boolean sendPaymentConfirmation(Payment payment) {
        byte[] allPaymentsOfUser = pdfService.generatePaymentsPdf(payment.getUser());
        ByteArrayResource resource = new ByteArrayResource(allPaymentsOfUser);

        Context context = new Context();
        context.setVariable("username", payment.getUser().getUsername());
        context.setVariable("amount", payment.getAmount());
        context.setVariable("eventName", payment.getEvent().getName());
        context.setVariable("transactionId", payment.getPaypalTransactionId());

        String text = templateEngine.process("email/payment-confirmation", context);

        return sendEmail(payment.getUser().getEmail(), "Zahlungsbestätigung", text, resource, "Zahlungen.pdf");
    }

    public boolean sendEventInvitation(User recipient, Event event, User inviter) {
        if (recipient.getEmail() == null || recipient.getEmail().isBlank()) {
            return false;
        }
        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("event", event);
        context.setVariable("inviter", inviter);

        String text = templateEngine.process("email/event-invitation", context);

        return sendEmail(recipient.getEmail(), "Einladung: " + event.getName(), text);
    }

    public boolean sendEventCancellation(User recipient, Event event, User organizer) {
        if (recipient.getEmail() == null || recipient.getEmail().isBlank()) {
            return false;
        }
        Context context = new Context();
        context.setVariable("recipient", recipient);
        context.setVariable("event", event);
        context.setVariable("organizer", organizer);

        String text = templateEngine.process("email/event-cancelled", context);

        return sendEmail(recipient.getEmail(), "Event abgesagt: " + event.getName(), text);
    }

    public boolean sendAccountStatusUpdateNotification(User user, String action) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("action", action);

        String subject;

        switch (action) {
            case "BLOCK":
                subject = "Dein Konto wurde deaktiviert";
                break;
            case "UNBLOCK":
                subject = "Dein Konto wurde reaktiviert";
                break;
            case "DELETE":
            default:
                subject = "Dein Konto wurde gelöscht";
                break;
        }

        String htmlContent = templateEngine.process("email/account-status-update", context);

        return sendEmail(user.getEmail(), subject, htmlContent);
    }

    public boolean sendRegistrationConfirmation(User user) {
        Context context = new Context();
        context.setVariable("user", user);

        String htmlContent = templateEngine.process("email/registration-confirmation", context);

        return sendEmail(user.getEmail(), "Willkommen bei uns!", htmlContent);
    }

    private boolean sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("oth.eventhub@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendEmail(String to, String subject, String text, ByteArrayResource attachment,
            String attachmentName) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("oth.eventhub@gmail.com");
            helper.setTo(to);
            // helper.setTo("oth.eventhub@gmail.com");
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.addAttachment(attachmentName, attachment, "application/pdf");

            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
