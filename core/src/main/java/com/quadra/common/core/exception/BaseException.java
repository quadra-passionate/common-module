package com.quadra.common.core.exception;

/**
 * The base exception class for all custom runtime exceptions in the application.
 *
 * <p>
 * This exception encapsulates an {@link ErrorCode}, which provides
 * a standardized way to represent an error code, message, and an HTTP status.
 *
 * <p>
 * All domain-specific exceptions should extend this class to ensure
 * consistent error handling across the application.
 *
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructs a new exception with the specified {@link ErrorCode}.
     *
     * <p>The message of the exception is derived from the provided
     * {@link ErrorCode}.</p>
     *
     * @param errorCode the error code associated with this exception
     * @see ErrorCode
     */
    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new exception with the specified {@link ErrorCode}
     * and cause.
     *
     * <p>The message of the exception is derived from the provided
     * {@link ErrorCode}, and the original cause is preserved for
     * debugging purposes.</p>
     *
     * @param errorCode the error code associated with this exception
     * @param cause     the underlying cause of the exception
     * @see ErrorCode
     * @see Throwable
     */
    protected BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the {@link ErrorCode} associated with this exception.
     *
     * @return the error code
     * @see ErrorCode
     */
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
