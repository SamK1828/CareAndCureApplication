package com.cac.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.model.Patient;
import com.cac.model.UserInfo;
import com.cac.repository.PatientRepository;

import jakarta.mail.MessagingException;

@Service
public class PatientService {

	@Autowired
	public PatientRepository patientRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	public Patient createPatient(Patient patient) throws UserNotFoundException {
		// Activate the new patient
		patient.setActive(true);
		// Save patient information to the repository
		Patient savedPatient = patientRepository.save(patient);

		// Create user information for login or account management
		UserInfo userInfo = new UserInfo("" + savedPatient.getPatientId(),
				patient.getPatientName(),
				"patient");
		try {
			userService.createUser(userInfo);
		} catch (Exception e) {
			patientRepository.delete(savedPatient);
			throw e;
		}

		try {
			emailService.sendPatientWelcomeEmail(savedPatient);
		} catch (MessagingException | MailSendException e) {
			e.printStackTrace();

		}

		return savedPatient;
	}

	public Patient updatePatientName(int id, String name) throws UserNotFoundException {
		Patient patient = patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));
		patient.setPatientName(name);
		return patientRepository.save(patient);
	}

	public Patient getPatientById(int id) throws UserNotFoundException {
		return patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));
	}

	public List<Patient> getAllPatients() {
		List<Patient> patientList = patientRepository.findAll();
		return patientList;
	}

	public List<Patient> getPatientsByName(String name) throws UserNotFoundException {
		List<Patient> patients = patientRepository.findByPatientNameContainingIgnoreCase(name);
		if (patients.isEmpty() || patients.size() == 0)
			throw new UserNotFoundException("Patients not found with name : " + name);
		return patients;
	}

	public List<Patient> getAllPatientByStatus(boolean flag) {
		return patientRepository.findByIsActive(flag);
	}

	public Patient updatePatient(int id, Patient patient) throws UserNotFoundException, MessagingException {
		Patient oldDetail = patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));

		boolean isUpdated = false;
		StringBuilder updateDetails = new StringBuilder();

		if (!oldDetail.getPatientName().equals(patient.getPatientName())) {
			oldDetail.setPatientName(patient.getPatientName());
			updateDetails.append("- Name updated to: ").append(patient.getPatientName()).append("\n");
			isUpdated = true;
		}
		if (!oldDetail.getEmailId().equals(patient.getEmailId())) {
			oldDetail.setEmailId(patient.getEmailId());
			updateDetails.append("- Email updated to: ").append(patient.getEmailId()).append("\n");
			isUpdated = true;
		}
		if (!oldDetail.getAllergies().equals(patient.getAllergies())) {
			oldDetail.setAllergies(patient.getAllergies());
			updateDetails.append("- Allergies updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getContactNumber().equals(patient.getContactNumber())) {
			oldDetail.setContactNumber(patient.getContactNumber());
			updateDetails.append("- Contact number updated to: ").append(patient.getContactNumber()).append("\n");
			isUpdated = true;
		}
		if (!oldDetail.getMedicalHistory().equals(patient.getMedicalHistory())) {
			oldDetail.setMedicalHistory(patient.getMedicalHistory());
			updateDetails.append("- Medical history updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getTreatments().equals(patient.getTreatments())) {
			oldDetail.setTreatments(patient.getTreatments());
			updateDetails.append("- Treatments updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getMedications().equals(patient.getMedications())) {
			oldDetail.setMedications(patient.getMedications());
			updateDetails.append("- Medications updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getAddress().equals(patient.getAddress())) {
			oldDetail.setAddress(patient.getAddress());
			updateDetails.append("- Address updated.\n");
			isUpdated = true;
		}
		if (oldDetail.getAge() != patient.getAge()) {
			oldDetail.setAge(patient.getAge());
			updateDetails.append("- Age updated to: ").append(patient.getAge()).append("\n");
			isUpdated = true;
		}
		if (oldDetail.getHasInsurance() || patient.getHasInsurance()!=oldDetail.getHasInsurance()) {
				oldDetail.setHasInsurance(patient.getHasInsurance());
				if (oldDetail.getInsurancePolicyNumber() == null
						|| !oldDetail.getInsurancePolicyNumber().equals(patient.getInsurancePolicyNumber())) {
					oldDetail.setInsurancePolicyNumber(patient.getInsurancePolicyNumber());
					updateDetails.append("- Insurance policy number updated to: ")
							.append(patient.getInsurancePolicyNumber()).append("\n");
					isUpdated = true;
				}
				if (oldDetail.getInsuranceProvider() == null
						|| !oldDetail.getInsuranceProvider().equals(patient.getInsuranceProvider())) {
					oldDetail.setInsuranceProvider(patient.getInsuranceProvider());
					updateDetails.append("- Insurance provider updated to: ").append(patient.getInsuranceProvider())
							.append("\n");
					isUpdated = true;
				}
				if (oldDetail.getInsuranceCoverageDetails() == null
						|| !oldDetail.getInsuranceCoverageDetails().equals(patient.getInsuranceCoverageDetails())) {
					oldDetail.setInsuranceCoverageDetails(patient.getInsuranceCoverageDetails());
					updateDetails.append("- Insurance coverage details updated.\n");
					isUpdated = true;
				}
				if (oldDetail.getInsuranceExpiryDate() == null
						|| !oldDetail.getInsuranceExpiryDate().equals(patient.getInsuranceExpiryDate())) {
					oldDetail.setInsuranceExpiryDate(patient.getInsuranceExpiryDate());
					updateDetails.append("- Insurance expiry date updated to: ")
							.append(patient.getInsuranceExpiryDate()).append("\n");
					isUpdated = true;
				}
				if (oldDetail.getInsuranceAmountLimit() == null
						|| !oldDetail.getInsuranceAmountLimit().equals(patient.getInsuranceAmountLimit())) {
					oldDetail.setInsuranceAmountLimit(patient.getInsuranceAmountLimit());
					updateDetails.append("- Insurance amount limit updated to: ")
							.append(patient.getInsuranceAmountLimit()).append("\n");
					isUpdated = true;
				}
			}
		if (!oldDetail.getGender().equals(patient.getGender())) {
			oldDetail.setGender(patient.getGender());
			updateDetails.append("- Gender details updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getDateOfBirth().equals(patient.getDateOfBirth())) {
			oldDetail.setDateOfBirth(patient.getDateOfBirth());
			updateDetails.append("- DOB details updated.\n");
			isUpdated = true;
		}

		if (isUpdated) {
			Patient updatedPatient = patientRepository.save(oldDetail);

			String subject = "Profile Update Notification - Care & Cure";
			String message = "<html><body>" +
					"<p>Dear <strong>" + updatedPatient.getPatientName() + "</strong>,</p>" +
					"<p>We hope this message finds you well.</p>" +
					"<p>This is to inform you that the following details in your Care & Cure profile have been successfully updated:</p>"
					+
					"<p><strong>" + updateDetails + "</strong></p><br>" +
					"<p>If you did not make these changes, please contact our support team immediately at <a href='mailto:support@careandcure.com'>support@careandcure.com</a> to secure your account.</p><br>"
					+
					"<p>Thank you for choosing Care & Cure. We are committed to providing you with the best healthcare experience.</p>"
					+
					"<p>Warm regards,<br>" +
					"The Care & Cure Team<br></p>" +
					"</body></html>";

			try {
				emailService.sendEmail(updatedPatient.getEmailId(), subject, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return updatedPatient;
		} else {
			throw new IllegalArgumentException("No changes detected to update the profile.");
		}
	}

	public Patient changeActive(int id) throws UserNotFoundException, MessagingException {
		Patient patient = patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));

		// Toggle active status
		patient.setActive(!patient.isActive());
		Patient updatedPatient = patientRepository.save(patient);

		String subject;
		String message;

		if (updatedPatient.isActive()) {
			// Account Activation Email
			subject = "Your Care & Cure Account Has Been Activated";
			message = "Dear " + updatedPatient.getPatientName() + ",\n\n" +
					"We are pleased to inform you that your Care & Cure account has been successfully activated.\n\n" +
					"You can now access all our healthcare services. If you need any assistance or have questions, please feel free to contact us.\n\n"
					+
					"Best regards,\n" +
					"The Care & Cure Team\n" +
					"Contact Us: support@careandcure.com\n" +
					"Phone: +1-800-555-CARE";

		} else {
			// Account Deactivation Email
			subject = "Your Care & Cure Account Has Been Deactivated";
			message = "Dear " + updatedPatient.getPatientName() + ",\n\n" +
					"We regret to inform you that your Care & Cure account has been deactivated. \n\n"
					+ "If you did not request this change, or if you wish to reactivate your account, please reach out to our support team.\n\n"
					+
					"We value your trust, and our team is here to assist you with any questions or concerns you may have.\n\n"
					+
					"Best regards,\n" +
					"The Care & Cure Team\n" +
					"Contact Us: support@careandcure.com\n" +
					"Phone: +1-800-555-CARE";
		}

		// Send the respective email
		try {
			emailService.sendEmail(updatedPatient.getEmailId(), subject, message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return updatedPatient;
	}

	// Fetch patients by age range
	public List<Patient> getPatientsByAgeRange(int minAge, int maxAge) {
		return patientRepository.findByAgeBetween(minAge, maxAge);
	}

	// Fetch patients by gender
	public List<Patient> getPatientsByGender(String gender) {
		return patientRepository.findByGenderIgnoreCase(gender);
	}

	// Get patient details for display
	public Optional<Patient> getPatientDetailsForDisplay(int patientId) {
		return patientRepository.findById(patientId);
	}

	//Get Patient by insurance provider 
	public List<Patient> getPatientsByInsuranceProvider(String provider){
		return patientRepository.findByInsuranceProviderContainingAllIgnoreCase(provider);
	}
}
