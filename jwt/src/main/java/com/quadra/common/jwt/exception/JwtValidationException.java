package com.quadra.common.jwt.exception;

import com.quadra.common.core.exception.BaseException;
import com.quadra.common.core.exception.ErrorCode;

/**
 * {@link BaseException} thrown when a JWT token fails validation.
 *
 * <p>
 * This {@link BaseException} is used to indicate issues such as expired tokens,
 * invalid signatures, or unsupported/malformed JWTs during parsing or validation.
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @see BaseException
 * @since 1.0.0
 */
public class JwtValidationException extends BaseException {

    /**
     * Constructs a new {@code JwtValidationException} instance with {@link ErrorCode}.
     *
     * @param errorCode the error code describing the validation error
     * @see ErrorCode
     */
    public JwtValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * Constructs a new {@code JwtValidationException} instance with the {@link ErrorCode} and cause.
     *
     * @param errorCode the error code describing the validation error
     * @param cause     the underlying cause of the exception
     * @see ErrorCode
     */
    public JwtValidationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
