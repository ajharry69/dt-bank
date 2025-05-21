package com.github.ajharry69.card.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PanValidator.class)
@Documented
public @interface Pan {

    String message() default "Invalid PAN format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

