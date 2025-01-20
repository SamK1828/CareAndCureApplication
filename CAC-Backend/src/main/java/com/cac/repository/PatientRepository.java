package com.cac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cac.model.Patient;

import java.util.List;


public interface PatientRepository extends JpaRepository<Patient, Integer> {


    // List<Patient> findByPaNameContainingIgnoreCase(String name);

    List<Patient> findByAgeBetween(int minAge, int maxAge);

    List<Patient> findByGenderIgnoreCase(String gender);

    List<Patient> findByPatientNameContainingIgnoreCase(String name);
	
	List<Patient> findByIsActive(boolean active);

    
    
}
