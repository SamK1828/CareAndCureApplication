package com.cac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.model.Doctor;
import com.cac.repository.DoctorRepository;

import java.util.*;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    // Fetch all doctors
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    // Fetch a doctor by ID
    public Optional<Doctor> getDoctorById(int doctorId) {
        return doctorRepository.findById(doctorId);
    }

    // Add a new doctor
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // Update doctor details
    public Doctor updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getDoctorId())) {
            throw new RuntimeException("Doctor with ID " + doctor.getDoctorId() + " not found");
        }
        return doctorRepository.save(doctor);
    }

    // Delete a doctor
    public void deleteDoctor(int doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor with ID " + doctorId + " not found");
        }
        doctorRepository.deleteById(doctorId);
    }

    // Get available doctors
    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByStatus(true); // Assuming 'true' indicates availability
    }

    // Fetch doctor's availability by ID
    public Map<String, List<String>> getDoctorAvailability(int doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor with ID " + doctorId + " not found"));
        return doctor.getAvailability();
    }

    // Fetch doctor name and availability for the frontend
    public Map<String, Object> getDoctorDetailsForDisplay(int doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor with ID " + doctorId + " not found"));

        Map<String, Object> doctorDetails = new HashMap<>();
        doctorDetails.put("doctorId", doctor.getDoctorId());
        doctorDetails.put("name", doctor.getName());
        doctorDetails.put("specialty", doctor.getSpecialty());
        doctorDetails.put("availability", doctor.getAvailability());
        doctorDetails.put("status", doctor.getStatus());

        return doctorDetails;
    }

    // Fetch doctors by specialization
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialty(specialization);
    }
}
