package com.github.ajharry69.account.utils.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {}) // No validator class needed, we use Pattern
@Documented
@Pattern(regexp = "^[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}$", message = "Invalid IBAN format")
public @interface Iban {

    String message() default "{com.github.ajharry69.account.utils.constraints.Iban.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}