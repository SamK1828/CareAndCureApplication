package com.cac.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cac.validators.DateOfBirthAgeValidator;

@Constraint(validatedBy = DateOfBirthAgeValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateOfBirth {
    String message() default "Age not matched with selected Date Of Birth.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
