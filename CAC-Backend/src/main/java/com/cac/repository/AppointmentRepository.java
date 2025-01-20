package com.cac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cac.dto.AppointmentDTO;
import com.cac.dto.CancelAppointmentRequest;
import com.cac.model.Appointment;
import com.cac.model.Doctor;
import com.cac.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByReasonOfCancellationIgnoreCase(String reasonOfCancellation);

    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(Doctor doctor, LocalDate date, LocalTime time);
}
