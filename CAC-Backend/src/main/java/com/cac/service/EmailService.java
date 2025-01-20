package com.cac.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cac.model.Patient;



@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Send confirmation email when an appointment is created
    public void sendAppointmentConfirmationEmail(String patientEmail, String patientName, String doctorName,
                                                 String appointmentDate, String appointmentTime) throws MessagingException {
        String subject = "Appointment Confirmation";
        String body = "<h3>Your appointment is confirmed</h3>" +
                "<p>Dear " + patientName + ",</p>" +
                "<p>Your appointment with Dr. " + doctorName + " is confirmed on " +
                appointmentDate + " at " + appointmentTime + ".</p>" +
                "<p>Thank you for choosing our service.</p>";
        sendEmail(patientEmail, subject, body);
    }

    // Send cancellation email when an appointment is canceled
    public void sendAppointmentCancellationEmail(String patientEmail, String patientName, String doctorName,
                                                 String appointmentDate, String appointmentTime, String reason) throws MessagingException {
        String subject = "Appointment Cancellation";
        String body = "<h3>Your appointment has been cancelled</h3>" +
                "<p>Dear " + patientName + ",</p>" +
                "<p>We regret to inform you that your appointment with Dr. " + doctorName + " scheduled for " +
                appointmentDate + " at " + appointmentTime + " has been cancelled.</p>" +
                "<p>Reason: " + reason + "</p>" +
                "<p>We apologize for the inconvenience caused.</p>";
        sendEmail(patientEmail, subject, body);
    }

    // General method to send email
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // True to allow HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email");
        }

    }
    public void sendAppointmentRescheduleEmail(String patientEmail, String patientName, String doctorName, String appointmentDate, String appointmentTime) throws MessagingException {
        String subject = "Your Appointment Has Been Rescheduled";
        String message = String.format("Dear %s,\n\nYour appointment with Dr. %s has been rescheduled to %s at %s.\n\nThank you,\nCare and Cure Team",
                patientName, doctorName, appointmentDate, appointmentTime);

        sendEmail(patientEmail, subject, message);
    }

    // public void sendEmail(String to, String subject, String text) throws MessagingException {
    //     MimeMessage message = mailSender.createMimeMessage();
    //     MimeMessageHelper helper = new MimeMessageHelper(message, true);
    //     helper.setTo(to);
    //     helper.setSubject(subject);
    //     helper.setText(text, true);

    //     mailSender.send(message);
    // }

    public void sendPatientWelcomeEmail(Patient savedPatient) throws MessagingException {
        String subject = "Welcome to Care & Cure";
        String message = String.format(
                "<html>" +
                        "<body>" +
                        "<h1>Welcome to Care & Cure</h1>" +
                        "<p>Dear <b>%s</b>,</p>" +
                        "<p>Thank you for registering with Care & Cure. Your patient ID is: <b>%d</b>.</p>" +
                        "<h3>ABOUT CARE & CURE HOSPITAL</h3>" +
                        "<p>Care & Cure is dedicated to improving health and well-being with compassionate, personalized healthcare services.</p>"
                        +
                        "<h3>OUR MISSION</h3>" +
                        "<p>To provide accessible, high-quality medical care with a focus on excellence, patient-centered treatment, and innovation.</p>"
                        +
                        "<h3>OUR VISION</h3>" +
                        "<p>To become a global leader in healthcare, known for trust, quality, and advancing medical research and education.</p>"
                        +
                        "<h3>OUR FACILITIES</h3>" +
                        "<ul>" +
                        "  <li>24/7 Emergency and Trauma Care</li>" +
                        "  <li>State-of-the-art Diagnostic and Laboratory Services</li>" +
                        "  <li>Advanced Surgical and Inpatient Facilities</li>" +
                        "  <li>Specialized Clinics for Cardiology, Orthopedics, and more</li>" +
                        "  <li>Preventive Health Programs and Wellness Services</li>" +
                        "</ul>" +
                        "<p>For any assistance or inquiries, feel free to reach out to us at: <a href='mailto:support@careandcure.com'>support@careandcure.com</a></p>"
                        +
                        "<p>Our team of highly skilled professionals is dedicated to providing you with world-class healthcare. We are honored to be part of your health journey.</p>"
                        +
                        "<p>Best regards,<br>Care & Cure Team</p>" +
                        "</body>" +
                        "</html>",
                savedPatient.getPatientName(), savedPatient.getPatientId());

        sendEmail(savedPatient.getEmailId(), subject, message);

    }

}
