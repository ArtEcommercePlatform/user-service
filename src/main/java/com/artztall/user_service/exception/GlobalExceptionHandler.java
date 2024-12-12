package com.artztall.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errorResponse.addFieldError(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public static class ErrorResponse {
        private List<FieldError> fieldErrors = new ArrayList<>();

        public void addFieldError(String field, String message) {
            fieldErrors.add(new FieldError(field, message));
        }

        public List<FieldError> getFieldErrors() {
            return fieldErrors;
        }
    }

    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        // Getters for JSON serialization
        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}