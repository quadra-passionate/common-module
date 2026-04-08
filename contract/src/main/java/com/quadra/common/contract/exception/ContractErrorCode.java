package com.quadra.common.contract.exception;

import com.quadra.common.core.exception.ErrorCode;

/**
 * Enumeration of error codes used in the contract module.
 *
 * <p>
 * Each enum constant represents a specific type of error that can occur
 * during request/response processing, including validation failures,
 * serialization issues, and unexpected internal errors.
 *
 * <p>
 * -1 as a status code means that it needs to be set by an administrator,
 * when used in an HTTP response.
 *
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
public enum ContractErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(500, "CA001", "An unexpected internal server error occurred."),
    INVALID_INPUT_VALUE(400, "CA002", "Invalid input value."),
    INVALID_OUTPUT_VALUE(500, "CA003", "Invalid output value."),
    JSON_WRITE_ERROR(500, "CA004", "Failed to write JSON response."),
    OTHER_MVC_ERROR(-1, "CA005", "MVC-related error occurred.");

    private final int status;
    private final String code;
    private final String message;

    ContractErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
