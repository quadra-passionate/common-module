package com.quadra.common.contract.handler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.common.contract.exception.ContractErrorCode;
import com.quadra.common.core.dto.ApiResponse;
import com.quadra.common.core.dto.ValidationException;
import com.quadra.common.core.exception.BaseException;
import com.quadra.common.core.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * {@code RestControllerAdvice} for integrating all responses from different microservices.
 *
 * <p>
 * Provides the automatic wrapping of controller responses into {@link ApiResponse}
 * and centralized exception handling. It only works on servlet-based applications.
 *
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @see RestControllerAdvice
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    private static final Set<HttpMethod> EXCLUDED_METHODS = Set.of(HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE);

    /**
     * Constructs a new {@code GlobalControllerAdvice} instance with the provided properties.
     *
     * @param objectMapper the Jackson object mapper
     */
    public GlobalControllerAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * Handles {@link BaseException} thrown from the application.
     *
     * <p>
     * Extracts the {@link ErrorCode} and returns a standardized error response.
     *
     * @param e the thrown business exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BaseException e) {

        ErrorCode errorCode = e.getErrorCode();
        int status = errorCode.getStatus();

        return ResponseEntity.status(status).body(ApiResponse.ofError(errorCode));
    }


    /**
     * Handles all uncaught exceptions.
     *
     * <p>
     * Returns a generic internal server error response using
     * {@link ContractErrorCode#INTERNAL_SERVER_ERROR INTERNAL_SERVER_ERROR}.
     *
     * @param e the unexpected exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOtherExceptions(Exception e) {

        ContractErrorCode errorCode = ContractErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.ofError(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * Handles validation errors that occur during method parameter validation.
     *
     * <p>
     * Distinguishes between input validation errors (400) and
     * return value validation errors (500), and maps them to appropriate
     * {@link ContractErrorCode}.
     *
     * <p>
     * Collects validation details into a list of {@link ValidationException}.
     *
     * @param ex the validation exception
     * @param headers HTTP headers
     * @param status HTTP status code
     * @param request current web request
     * @return a {@link ResponseEntity} containing validation error details
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        // status.value() == 400 -> input validation error
        // status.value() == 500 -> validation error on a return value
        ContractErrorCode errorCode = status.value() == 400 ? ContractErrorCode.INVALID_INPUT_VALUE : ContractErrorCode.INVALID_OUTPUT_VALUE;
        List<ValidationException> details = new ArrayList<>();

        ex.visitResults(new HandlerMethodValidationException.Visitor() {

            @Override
            public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
                String candidate = cookieValue.name();
                String key = !candidate.isBlank() ? candidate : result.getMethodParameter().getParameterName();

                add(details, key, result);

            }

            @Override
            public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
                String candidate = matrixVariable.name();
                String key = !candidate.isBlank() ? candidate : result.getMethodParameter().getParameterName();

                add(details, key, result);
            }

            @Override
            public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
                add(details, errors);
            }

            @Override
            public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
                String candidate = pathVariable.name();
                String key = !candidate.isBlank() ? candidate : result.getMethodParameter().getParameterName();

                add(details, key, result);
            }

            @Override
            public void requestBody(RequestBody requestBody, ParameterErrors errors) {
                add(details, errors);
            }

            @Override
            public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
                String key = result.getMethodParameter().getParameterName();
                add(details, key, result);
            }

            @Override
            public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
                String candidate = requestHeader.name();
                String key = !candidate.isBlank() ? candidate : result.getMethodParameter().getParameterName();

                add(details, key, result);
            }

            @Override
            public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
                String candidate = requestParam != null ? requestParam.name() : "";
                String key = !candidate.isBlank() ? candidate : result.getMethodParameter().getParameterName();

                add(details, key, result);
            }

            @Override
            public void requestPart(RequestPart requestPart, ParameterErrors errors) {
                add(details, errors);
            }

            @Override
            public void other(ParameterValidationResult result) {
                String key = result.getMethodParameter().getParameterName();

                add(details, key, result);
            }
        });


        return ResponseEntity.status(status).body(ApiResponse.ofError(status.value(), errorCode.getCode(), errorCode.getMessage(), details));
    }

    /**
     * Handles validation errors for {@code @RequestBody} or {@code @ModelAttribute}.
     *
     * <p>
     * Extracts field-level errors and converts them into
     * {@link ValidationException} objects.
     *
     * @param ex the validation exception
     * @param headers HTTP headers
     * @param status HTTP status code
     * @param request current web request
     * @return a {@link ResponseEntity} containing validation error details
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        ContractErrorCode errorCode = ContractErrorCode.INVALID_INPUT_VALUE;

        List<ValidationException> details = ex.getBindingResult().getAllErrors().stream().map(error -> new ValidationException(resolveKey(error), error.getDefaultMessage())).toList();

        return ResponseEntity.status(status).body(ApiResponse.ofError(status.value(), errorCode.getCode(), errorCode.getMessage(), details));
    }

    /**
     * Creates a standardized {@link ResponseEntity} for Spring MVC internal exceptions.
     *
     * <p>
     * This method overrides the default behavior of
     * {@link ResponseEntityExceptionHandler#createResponseEntity(Object, HttpHeaders, HttpStatusCode, WebRequest)}
     * to ensure that all error responses conform to the application's {@link ApiResponse} format.
     *
     * <p>If the response body is an instance of {@link ProblemDetail}, its detail message
     * is extracted. Otherwise, the body is converted to a string (if not {@code null})
     * and used as the error message.
     *
     * <p>The error is mapped to {@link ContractErrorCode#OTHER_MVC_ERROR}.
     *
     * @param body the response body, possibly {@link ProblemDetail} or another object
     * @param headers the HTTP headers to be written to the response
     * @param statusCode the HTTP status code
     * @param request the current web request
     * @return a {@link ResponseEntity} containing a standardized error {@link ApiResponse}
     */
    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        ContractErrorCode errorCode = ContractErrorCode.OTHER_MVC_ERROR;
        String message;

        if (body instanceof ProblemDetail pd) {
            message = pd.getDetail();
        } else {
            message = body != null ? body.toString() : null;
        }
        return ResponseEntity.status(statusCode).body(ApiResponse.ofError(statusCode.value(), errorCode.getCode(), message));
    }


    /**
     * Determines whether this advice applies to the given controller method.
     *
     * <p>
     * This implementation excludes classes from the {@code org.springdoc}
     * package to avoid interfering with OpenAPI/Swagger documentation generation.
     *
     * @param returnType the return type of the controller method
     * @param converterType the selected {@link HttpMessageConverter} type
     * @return {@code true} if this advice should be applied; {@code false} otherwise
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        log.debug("supports method called");
        return !returnType.getContainingClass().getPackageName().startsWith("org.springdoc");
    }


    /**
     * Intercepts and modifies the response body before it is written to the client.
     *
     * <p>
     * This method wraps successful responses into {@link ApiResponse} unless:
     * <ul>
     *     <li>The body is already an instance of {@link ApiResponse}</li>
     *     <li>The HTTP method does not support a body (HEAD, OPTIONS, TRACE)</li>
     *     <li>The response is not a servlet-based response</li>
     *     <li>The HTTP status code does not allow a body (1xx, 3xx, 204)</li>
     * </ul>
     *
     * <p>
     * Special handling is applied when using {@link StringHttpMessageConverter},
     * where the response is serialized manually into a JSON string.
     *
     * @param body the response body
     * @param returnType the return type of the controller method
     * @param selectedContentType the selected content type
     * @param selectedConverterType the selected message converter type
     * @param request the current server request
     * @param response the current server response
     * @return the modified (or original) response body
     */
    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        log.debug("beforeBodyWrite method called with (content: {}, converter: {})", selectedContentType, selectedConverterType);
        log.debug("body: {}", body instanceof byte[] ? new String((byte[]) body, StandardCharsets.UTF_8) : null);

        Class<?> innerType = body != null ? body.getClass() : Object.class;
        log.debug("innerType: {}", innerType);

        // if body is instance of ApiResponse
        if (body instanceof ApiResponse) {
            return body;
        }

        // exclude HEAD, OPTIONS, TRACE
        HttpMethod httpMethod = request.getMethod();
        if (EXCLUDED_METHODS.contains(httpMethod)) {
            return body;
        }

        // check content-type if needed

        // handle servlet responses only
        if (!(response instanceof ServletServerHttpResponse ssr)) {
            return body;
        }

        int status = ssr.getServletResponse().getStatus();

        // exclude body-less response
        if (isBodyLessStatus(status)) {
            return body;
        }

        ApiResponse<?> apiResponse = ApiResponse.ofSuccess(status, body);

        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(apiResponse);
            } catch (JacksonException e) {
                throw new RuntimeException(e);
            }
        }

        return apiResponse;
    }

    /**
     * Determines whether the given HTTP status code should not contain a response body.
     *
     * <p>This includes:
     * <ul>
     *     <li>1xx (Informational)</li>
     *     <li>3xx (Redirection)</li>
     *     <li>204 (No Content)</li>
     * </ul>
     *
     * @param status the HTTP status code
     * @return {@code true} if the response should not contain a body; {@code false} otherwise
     */
    private boolean isBodyLessStatus(int status) {
        return status / 100 == 1 || status / 100 == 3 || status == 204;
    }

    /**
     * Adds validation errors from {@link ParameterValidationResult} to the given list.
     *
     * <p>
     * Each error is converted into a {@link ValidationException} using the provided key.
     *
     * @param details the list to which validation errors will be added
     * @param key the field or parameter name associated with the errors
     * @param result the validation result containing errors
     */
    private void add(List<ValidationException> details, String key, ParameterValidationResult result) {
        result.getResolvableErrors().forEach(error -> details.add(new ValidationException(key, error.getDefaultMessage())));
    }

    /**
     * Adds validation errors from {@link ParameterErrors} to the given list.
     *
     * <p>
     * Each error is converted into a {@link ValidationException}, where
     * the key is resolved using {@link #resolveKey(ObjectError)}.
     *
     * @param details the list to which validation errors will be added
     * @param errors the container holding validation errors
     */
    private void add(List<ValidationException> details, ParameterErrors errors) {
        errors.getAllErrors().forEach(error -> details.add(new ValidationException(resolveKey(error), error.getDefaultMessage())));
    }

    /**
     * Resolves the key (field or object name) from the given {@link ObjectError}.
     *
     * <p>
     * If the error is a {@link FieldError}, the field name is returned.
     * Otherwise, the object name is used.
     *
     * @param error the validation error
     * @return the resolved key representing the field or object name
     */
    private String resolveKey(ObjectError error) {
        return error instanceof FieldError fe ? fe.getField() : error.getObjectName();
    }
}
