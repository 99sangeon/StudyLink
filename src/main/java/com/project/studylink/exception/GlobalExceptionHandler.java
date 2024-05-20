package com.project.studylink.exception;

import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.ErrorResponse;
import com.project.studylink.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.studylink.enums.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleCustomException(BusinessException ex) {
        log.warn("handleBusinessException : {} -> ErrorCode : {}", ex.toString(), ex.getErrorCode().name());
        return handleExceptionInternal(ex.getErrorCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("handleBadCredentialsException : {}", ex.toString());
        return handleExceptionInternal(LOGIN_FAIL);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpectedException(Exception ex) {
        log.warn("handleUnexpectedException : {}", ex.toString());
        log.warn("handleUnexpectedException : {}", ex.getStackTrace());
        return handleExceptionInternal(INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("handleMethodArgumentNotValid : {}", ex.toString());
        return handleExceptionInternal(ex.getFieldErrors());
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("handleNoResourceFoundException : {}", ex.toString());
        return handleExceptionInternal(NOT_FOUND_RESOURCE);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ErrorResponse error = new ErrorResponse("REQUEST_ERROR", ex.getMessage());
        return ResponseEntity.status(statusCode)
                .body(ApiResponse.error(error));
    }
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        ErrorResponse error = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(error));
    }

    private ResponseEntity<Object> handleExceptionInternal(List<FieldError> fieldErrors) {
        Map<String, String> fields = new HashMap<>();

        fieldErrors.forEach(fieldError -> fields
                .put(fieldError.getField(), fieldError.getDefaultMessage()));

        ErrorResponse error = new ErrorResponse(FIELD_ERROR, fields);
        return ResponseEntity.status(FIELD_ERROR.getHttpStatus())
                .body(ApiResponse.error(error));
    }
}