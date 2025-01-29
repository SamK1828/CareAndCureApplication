package com.cac.client.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Patient {

	private int patientId;
	private String patientName;
	private int age;
	private LocalDate dateOfBirth;
	private String gender;
	private String contactNumber;
	private String emailId;
	private String medicalHistory;
	private String address;
	private String allergies;
	private String medications;
	private String treatments;
	
	// Insurance details fields
	private Boolean hasInsurance; // The 'hasInsurance' flag indicates whether the patient has insurance
	private String insuranceProvider;
	private String insurancePolicyNumber;
	private LocalDate insuranceExpiryDate;
	private String insuranceCoverageDetails;
    private Double insuranceAmountLimit;
	private boolean isActive;

	public boolean isActive() {
		return isActive;
	}

}
