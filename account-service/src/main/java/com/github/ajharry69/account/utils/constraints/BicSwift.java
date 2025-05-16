package com.github.ajharry69.account.utils.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "Invalid BIC/SWIFT format")
public @interface BicSwift {

    String message() default "{com.github.ajharry69.account.utils.constraints.BicSwift.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}