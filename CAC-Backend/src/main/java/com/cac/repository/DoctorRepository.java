package com.cac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cac.model.Doctor;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    List<Doctor> findByStatus(Boolean status);

    List<Doctor> findBySpecialty(String specialty);
}
