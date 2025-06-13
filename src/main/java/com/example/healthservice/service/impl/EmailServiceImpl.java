package com.example.healthservice.service.impl;

import com.example.healthservice.model.Appointment;
import com.example.healthservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final String FROM_EMAIL = "noreply@healthservice.com";

    @Override
    public void sendAppointmentConfirmation(Appointment appointment, String calendarLink) {
        try {
            // Send email to user
            sendUserConfirmation(appointment, calendarLink);
            
            // Send email to entity
            sendEntityConfirmation(appointment, calendarLink);
        } catch (MessagingException e) {
            log.error("Error sending appointment confirmation emails for appointment: {}", appointment.getId(), e);
        }
    }

    private void sendUserConfirmation(Appointment appointment, String calendarLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        context.setVariable("date", appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        context.setVariable("time", appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        context.setVariable("calendarLink", calendarLink);

        String emailContent = templateEngine.process("appointment-confirmation-user", context);
        sendEmail(appointment.getUserEmail(), "Appointment Confirmation", emailContent);
    }

    private void sendEntityConfirmation(Appointment appointment, String calendarLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        context.setVariable("date", appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        context.setVariable("time", appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        context.setVariable("calendarLink", calendarLink);

        String emailContent = templateEngine.process("appointment-confirmation-entity", context);
        sendEmail(appointment.getEntityEmail(), "New Appointment Notification", emailContent);
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(FROM_EMAIL);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        
        mailSender.send(message);
        log.info("Email sent to: {}", to);
    }
} 