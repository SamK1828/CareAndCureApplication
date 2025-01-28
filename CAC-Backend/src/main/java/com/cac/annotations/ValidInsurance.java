package com.cac.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cac.validators.InsuranceValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = InsuranceValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInsurance {

    String message() default "Invalid Insurance Details";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
