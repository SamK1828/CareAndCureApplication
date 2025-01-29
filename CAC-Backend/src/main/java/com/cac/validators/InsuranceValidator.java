package com.cac.validators;

import com.cac.annotations.ValidInsurance;
import com.cac.model.Patient;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InsuranceValidator implements ConstraintValidator<ValidInsurance, Patient> {

    @Override
    public void initialize(ValidInsurance constraintAnnotation) {
    }

    @Override
    public boolean isValid(Patient patient, ConstraintValidatorContext context) {
        if (patient == null) {
            return true; // Consider null objects as valid, but ensure nullability in fields.
        }

        if (Boolean.TRUE.equals(patient.getHasInsurance())) {
            boolean valid = true;
            if (patient.getInsuranceProvider() == null || patient.getInsuranceProvider().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Insurance Provider is required")
                        .addPropertyNode("insuranceProvider").addConstraintViolation();
                valid = false;
            }
            if (patient.getInsurancePolicyNumber() == null || patient.getInsurancePolicyNumber().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Insurance Policy Number is required")
                        .addPropertyNode("insurancePolicyNumber").addConstraintViolation();
                valid = false;
            }
            if (patient.getInsuranceExpiryDate() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Insurance Expiry Date is required")
                        .addPropertyNode("insuranceExpiryDate").addConstraintViolation();
                valid = false;
            }
            if (patient.getInsuranceCoverageDetails() == null || patient.getInsuranceCoverageDetails().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Insurance Coverage Details are required")
                        .addPropertyNode("insuranceCoverageDetails").addConstraintViolation();
                valid = false;
            }
            if (patient.getInsuranceAmountLimit() == null || patient.getInsuranceAmountLimit()<=0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Insurance amount limit is required")
                        .addPropertyNode("insuranceAmountLimit").addConstraintViolation();
                valid = false;
            }
            return valid;
        }
        return true;
    }
}

