package com.cac.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import com.cac.dto.AppointmentDTO;
import com.cac.dto.CancelAppointmentRequest;
import com.cac.model.Appointment;
import com.cac.model.Doctor;
import com.cac.model.Patient;
import com.cac.repository.AppointmentRepository;
import com.cac.repository.DoctorRepository;
import com.cac.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmailService emailService;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));
        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> getReasonOfCancellation(String reason) {
        return appointmentRepository.findByReasonOfCancellationIgnoreCase(reason);
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor with ID " + doctorId + " not found"));
        return appointmentRepository.findByDoctor(doctor);
    }

    public Optional<Appointment> getAppointmentById(int appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public Appointment createAppointment(Appointment appointment) throws MessagingException {
        boolean isAvailable = isTimeSlotAvailable(
                appointment.getDoctor().getDoctorId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime());

        if (!isAvailable) {
            throw new IllegalStateException("The selected time slot is already booked. Please choose another time.");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }

        appointment.setStatus("Scheduled");
        Appointment savedAppointment = appointmentRepository.save(appointment);

        sendAppointmentConfirmationEmail(savedAppointment);
        return savedAppointment;
    }

    public Appointment updateAppointment(Appointment appointment) throws MessagingException {
        if (!appointmentRepository.existsById(appointment.getAppointmentId())) {
            throw new IllegalArgumentException("Appointment with ID " + appointment.getAppointmentId() + " not found.");
        }

        boolean isAvailable = isTimeSlotAvailable(
                appointment.getDoctor().getDoctorId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime());

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }

        if (!isAvailable) {
            throw new IllegalStateException("The selected time slot is already booked. Please choose another time.");
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        sendAppointmentConfirmationEmail(updatedAppointment);
        return updatedAppointment;
    }

    public boolean isTimeSlotAvailable(int doctorId, LocalDate appointmentDate, LocalTime appointmentTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor with ID " + doctorId + " not found"));

        return !appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTime(doctor, appointmentDate,
                appointmentTime);
    }

    public void cancelAppointment(int appointmentId, String reason) throws MessagingException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment with ID " + appointmentId + " not found"));

        appointment.setStatus("Cancelled");
        appointment.setReasonOfCancellation(reason);
        appointmentRepository.save(appointment);

        sendAppointmentCancellationEmail(appointment);
    }

    public void deleteAppointmentById(int appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new IllegalArgumentException("Appointment with ID " + appointmentId + " not found.");
        }
        appointmentRepository.deleteById(appointmentId);
    }

    @Transactional
    public Appointment rescheduleAppointment(int appointmentId, LocalDate newDate, LocalTime newTime)
            throws MessagingException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Appointment with ID " + appointmentId + " not found."));

        // if(appointment.getAppointmentDate().isBefore(LocalDate.now())) {
        // throw new IllegalArgumentException("Appointment date cannot be in the
        // past.");
        // }
        boolean isAvailable = isTimeSlotAvailable(appointment.getDoctor().getDoctorId(), newDate, newTime);
        if (!isAvailable) {
            throw new IllegalStateException("The selected time slot is already booked. Please choose another time.");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);
        appointment.setStatus("Rescheduled");
        appointment.setReasonOfCancellation(null);
        System.out.println(appointment);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        // sendAppointmentRescheduleEmail(updatedAppointment);
        return updatedAppointment;
    }

    private void sendAppointmentConfirmationEmail(Appointment appointment) throws MessagingException {
        emailService.sendAppointmentConfirmationEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPatientName(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDate().toString(),
                appointment.getAppointmentTime().toString());
    }

    private void sendAppointmentCancellationEmail(Appointment appointment) throws MessagingException {
        emailService.sendAppointmentCancellationEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPatientName(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDate().toString(),
                appointment.getAppointmentTime().toString(),
                appointment.getReasonOfCancellation());
    }

    private void sendAppointmentRescheduleEmail(Appointment appointment) throws MessagingException {
        emailService.sendAppointmentRescheduleEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPatientName(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDate().toString(),
                appointment.getAppointmentTime().toString());
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    public List<Appointment> getNoShowAppointments() {
        List<Appointment> notPresentAppointments = new ArrayList<>();
        List<Appointment> allAppointments = getAllAppointments();

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        for (Appointment appointment : allAppointments) {
            if (appointment.getStatus().equalsIgnoreCase("Scheduled")
                    || appointment.getStatus().equalsIgnoreCase("Rescheduled")
                    || appointment.getReasonOfCancellation().toLowerCase().contains("not present")
                    || appointment.getReasonOfCancellation().toLowerCase().contains("notpresent")) {

                if (appointment.getAppointmentDate().isBefore(currentDate))
                    notPresentAppointments.add(appointment);

            } else if (appointment.getAppointmentDate().equals(currentDate)
                    && appointment.getAppointmentTime().isBefore(currentTime)) {
                notPresentAppointments.add(appointment);
            }
        }

        return notPresentAppointments;
    }
}
