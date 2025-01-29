package com.cac.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

import com.cac.annotations.ValidDateOfBirth;
import com.cac.model.Patient;

public class DateOfBirthAgeValidator implements ConstraintValidator<ValidDateOfBirth, Patient> {

    @Override
    public boolean isValid(Patient patient, ConstraintValidatorContext context) {
        if (patient == null) {
            return true; // Default behavior for null objects; validation will pass.
        }

        LocalDate dateOfBirth = patient.getDateOfBirth();
        Integer age = patient.getAge(); // Change `int` to `Integer` for nullable checks

        if (dateOfBirth == null) {
            // Disable default message and add a custom message
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Date of birth must be required")
                    .addPropertyNode("dateOfBirth").addConstraintViolation();
            return false;
        }

        LocalDate currentDate = LocalDate.now();

        // Check if dateOfBirth is in the future
        if (dateOfBirth.isAfter(currentDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Date of birth must be valid")
                    .addConstraintViolation();
            return false;
        }

        // Calculate age from dateOfBirth
        // int calculatedAge = currentDate.getYear()-dateOfBirth.getYear();

        // if (calculatedAge < age) {
        // context.disableDefaultConstraintViolation();
        // context.buildConstraintViolationWithTemplate("Age mismatched with date of
        // birth")
        // .addConstraintViolation();
        // return false;
        // }

        Period ageDifference = Period.between(dateOfBirth, currentDate);
        int calculatedAge = ageDifference.getYears();

        if (calculatedAge < age) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid Date of Birth or Age")
                    .addConstraintViolation();
            return false;
        }
        return true;

    }
}
