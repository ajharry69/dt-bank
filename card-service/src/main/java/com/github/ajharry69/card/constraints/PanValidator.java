package com.github.ajharry69.card.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class PanValidator implements ConstraintValidator<Pan, String> {
    @Override
    public boolean isValid(String pan, ConstraintValidatorContext context) {
        if (pan == null || pan.trim().isEmpty()) {
            return true; // Allow empty or null PAN if @NotBlank is used separately
        }

        pan = pan.replaceAll("-","");

        if (!pan.matches("^[0-9]{13,19}$")) {
            return false;
        }

        return isValidLuhn(pan);
    }

    /**
     * Ref - <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">Luhn Algorithm</a>
     *
     * @param pan Primary Account Number to validate. A PAN usually has 13 to 19 digits, although 16 digits
     *            are the most common.
     *            <p>
     *            The structure follows the ISO/IEC 7812 standard and includes:
     *            <p>
     *            •Issuer Identification Number (IIN) or Bank Identification Number (BIN): The first
     *            6 digits, identifying the card issuer (e.g., Visa, Mastercard).
     *            <p>
     *            •Account Number: The digits following the IIN/BIN, identifying the cardholder's
     *            account.
     *            <p>
     *            •Check Digit: The last digit, calculated using the Luhn algorithm (also known as
     *            the "mod 10" algorithm) to help detect errors or fraud.
     * @return boolean indicating if the PAN is valid or not.
     */
    boolean isValidLuhn(String pan) {
        return true; // TODO: implement algorithm...
    }
}