package com.quadra.common.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Represents a standardized error response returned by the API.
 *
 * <p>
 * This object contains an error code, a human-readable message,
 * and optional validation details.
 *
 * <p>
 * The validation details are populated when errors occur during
 * Jakarta Bean Validation (e.g., constraint violations).
 *
 * <p>
 * Fields with empty values are excluded from the JSON response.
 *
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private String code;
    private String message;
    private List<ValidationException> details;

    /**
     * Constructs a new {@code ErrorResponse} instance with all fields.
     *
     * @param code the error code
     * @param message the error message
     * @param details the list of validation error details
     */
    private ErrorResponse(String code, String message, List<ValidationException> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    /**
     * A static method that creates an {@code ErrorResponse} instance with all fields.
     *
     * @param code the error code
     * @param message the error message
     * @param details the list of validation error details
     * @return a new {@code ErrorResponse} instance
     */
    public static ErrorResponse of(String code, String message, List<ValidationException> details) {
        return new ErrorResponse(code, message, details);
    }

    /**
     * A static method that creates an {@code ErrorResponse} instance with code and message only.
     *
     * @param code the error code
     * @param message the error message
     * @return a new {@code ErrorResponse} instance
     */
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null);
    }

    /**
     * A static method that creates an {@code ErrorResponse} instance with code and validation error details only.
     * @param code the error code
     * @param details the list of validation error details
     * @return a new {@code ErrorResponse} instance
     */
    public static ErrorResponse of(String code, List<ValidationException> details) {
        return new ErrorResponse(code, null, details);
    }

    /**
     * Returns the error code.
     *
     * @return the error code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the validation error details
     *
     * @return the list of validation error details
     */
    public List<ValidationException> getDetails() {
        return this.details;
    }
}
