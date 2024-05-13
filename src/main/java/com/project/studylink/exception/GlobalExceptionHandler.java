package com.project.studylink.exception;

import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.response.ErrorResponse;
import com.project.studylink.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.studylink.enums.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleCustomException(BusinessException ex) {
        log.warn("handleBusinessException : {}", ex.toString());
        return handleExceptionInternal(ex.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("handleMethodArgumentNotValidException : {}", ex.toString());
        return handleExceptionInternal(ex.getFieldErrors());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("handleNoResourceFoundException : {}", ex.toString());
        return handleExceptionInternal(NOT_FOUND_RESOURCE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpectedException(Exception ex) {
        log.warn("handleUnexpectedException : {}", ex.toString());
        return handleExceptionInternal(INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> handleExceptionInternal(ErrorCode errorCode) {
        ErrorResponse error = new ErrorResponse(errorCode);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(error));
    }

    private ResponseEntity<?> handleExceptionInternal(List<FieldError> fieldErrors) {
        Map<String, String> fields = new HashMap<>();

        fieldErrors.forEach(fieldError -> fields
                .put(fieldError.getField(), fieldError.getDefaultMessage()));

        ErrorResponse error = new ErrorResponse(FIELD_ERROR, fields);

        return ResponseEntity.status(FIELD_ERROR.getHttpStatus())
                .body(ApiResponse.error(error));
    }
}
