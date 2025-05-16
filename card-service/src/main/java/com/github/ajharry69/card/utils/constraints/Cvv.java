package com.github.ajharry69.card.utils.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {}) // No validator class needed, we use Pattern
@Documented
@Pattern(regexp = "^\\d{3}$", message = "Invalid CVV format")
public @interface Cvv {

    String message() default "{com.github.ajharry69.card.utils.constraints.Cvv.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}