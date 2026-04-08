package com.quadra.common.core.exception;

/**
 * Defines a contract for standardized error codes in the applications.
 *
 * <p>
 * Implementations of this interface (typically enums) provide a consistent way
 * to represent errors with an HTTP status, a code that can be understood by administrators,
 * and a human-readable message.
 *
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
public interface ErrorCode {

    /**
     * Returns the HTTP status code associated with the error.
     *
     * @return the HTTP status code
     */
    int getStatus();

    /**
     * Returns an error code used by administrators for troubleshooting.
     *
     * @return the error code
     */
    String getCode();

    /**
     * Returns a human-readable error message.
     *
     * @return the error message
     */
    String getMessage();
}
