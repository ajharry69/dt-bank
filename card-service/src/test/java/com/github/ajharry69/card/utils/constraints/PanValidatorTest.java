package com.github.ajharry69.card.utils.constraints;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PanValidatorTest {

    private PanValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PanValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Nested
    class IsValidLuhn {
        @ParameterizedTest
        @ValueSource(
                strings = {
                        "4111111111111234", // 16-digit PAN
                        "378282246310005", // 15-digit PAN (American Express)
                        "6011000000000000000", // 19-digit PAN (Discover)
                }
        )
        void validPan(String pan) {
            assertTrue(validator.isValidLuhn(pan));
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                        "4111111111111235", // 16-digit PAN (check digit incorrect)
                        "378282246310006", // 15-digit PAN (check digit incorrect)
                        "6011000000000000001", // 19-digit PAN (check digit incorrect)
                }
        )
        @Disabled(value = "Algorithm is yet to be implemented.")
        void invalidPan(String pan) {
            assertFalse(validator.isValidLuhn(pan));
        }
    }

    @Nested
    class IsValid {
        @ParameterizedTest
        @ValueSource(strings = {"   "})
        @NullSource
        @EmptySource
        void shouldReturnTrue(String pan) {
            assertTrue(validator.isValid(pan, context));
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                        "4111111111111234", // 16-digit PAN
                        "378282246310005", // 15-digit PAN (American Express)
                        "6011000000000000000", // 19-digit PAN (Discover)
                }
        )
        void validPan(String pan) {
            assertTrue(validator.isValid(pan, context));
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                        "4111111111111235", // 16-digit PAN (check digit incorrect)
                        "378282246310006", // 15-digit PAN (check digit incorrect)
                        "6011000000000000001", // 19-digit PAN (check digit incorrect)
                        "123456789012", // Too short
                        "123456789012345678901", // Too long
                        "4111-1111-1111-1234", // Non-digit characters
                        "411111111111123A", // Non-digit characters
                }
        )
        @Disabled(value = "Algorithm is yet to be implemented.")
        void invalidPan(String pan) {
            assertFalse(validator.isValid(pan, context));
        }
    }
}
