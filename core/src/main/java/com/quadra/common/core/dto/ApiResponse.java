package com.quadra.common.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quadra.common.core.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a standardized API response in the applications.
 *
 * <p>
 * This wrapper class provides a consistent structure for both successful
 * and error responses returned by the API.
 * <p>
 * A response contains:
 * <ul>
 *   <li>{@code success} - indicates whether the request was successful</li>
 *   <li>{@code status} - HTTP status code</li>
 *   <li>{@code data} - response payload (for successful requests)</li>
 *   <li>{@code error} - error details (for failed requests)</li>
 *   <li>{@code timestamp} - the time the response was generated</li>
 * </ul>
 * <p>
 * For error responses, the {@code error} field contains an {@link ErrorResponse},
 * which may include validation details from Jakarta Bean Validation.
 * <p>
 * Fields with {@code null} values are excluded from the JSON response.
 *
 * @param <T> the type of the response payload
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private T data;
    private ErrorResponse error;
    private LocalDateTime timestamp;

    private ApiResponse(boolean success, int status, T data, ErrorResponse error, LocalDateTime timestamp) {
        this.success = success;
        this.status = status;
        this.data = data;
        this.error = error;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public static <T> ApiResponse<T> ofSuccess(int status, T data, LocalDateTime timestamp) {
        return new ApiResponse<>(true, status, data, null, timestamp);
    }

    public static <T> ApiResponse<T> ofSuccess(int status, T data) {
        return new ApiResponse<>(true, status, data, null, null);
    }

    public static <T> ApiResponse<T> ofSuccess(T data, LocalDateTime timestamp) {
        return new ApiResponse<>(true, 200, data, null, timestamp);
    }

    public static <T> ApiResponse<T> ofSuccess(T data) {
        return new ApiResponse<>(true, 200, data, null, null);
    }


    public static <T> ApiResponse<T> ofError(int status, ErrorResponse error, LocalDateTime timestamp) {
        return new ApiResponse<>(false, status, null, error, timestamp);
    }

    public static <T> ApiResponse<T> ofError(int status, ErrorResponse error) {
        return new ApiResponse<>(false, status, null, error, null);
    }

    public static <T> ApiResponse<T> ofError(int status, String code, String message, LocalDateTime timestamp) {
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, message), timestamp);
    }

    public static <T> ApiResponse<T> ofError(int status, String code, String message) {
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, message), null);
    }

    public static <T> ApiResponse<T> ofError(int status, String code, String message, List<ValidationException> details, LocalDateTime timestamp) {
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, message, details), timestamp);
    }

    public static <T> ApiResponse<T> ofError(int status, String code, String message, List<ValidationException> details) {
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, message, details), null);
    }

    public static <T> ApiResponse<T> ofError(int status, String code, List<ValidationException> details, LocalDateTime timestamp) {
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, details), timestamp);
    }

    public static <T> ApiResponse<T> ofError(int status, String code, List<ValidationException> details) {
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, details), null);
    }

    public static <T> ApiResponse<T> ofError(ErrorCode errorCode, LocalDateTime timestamp) {
        int status = errorCode.getStatus();
        String code = errorCode.getCode();
        String message = errorCode.getMessage();
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, message), timestamp);
    }

    public static <T> ApiResponse<T> ofError(ErrorCode errorCode) {
        int status = errorCode.getStatus();
        String code = errorCode.getCode();
        String message = errorCode.getMessage();
        return new ApiResponse<>(false, status, null, ErrorResponse.of(code, message), null);
    }
}
