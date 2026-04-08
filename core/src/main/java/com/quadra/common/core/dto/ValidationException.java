package com.quadra.common.core.dto;

/**
 * Represents an error that occurs during Jakarta Bean Validation.
 *
 * <p>
 * Each instance contains information about a specific constraint violaation,
 * including the target field and the associated error message.
 *
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
public class ValidationException {
    private final String target;
    private final String message;

    public ValidationException(String target, String message) {
        this.target = target;
        this.message = message;
    }

    /**
     * Returns the target field or parameter that caused the validation error.
     *
     * @return the name of the invalid field or parameter
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * Returns the validation error message.
     *
     * @return the error message describing the constraint violation
     */
    public String getMessage() {
        return this.message;
    }
}
